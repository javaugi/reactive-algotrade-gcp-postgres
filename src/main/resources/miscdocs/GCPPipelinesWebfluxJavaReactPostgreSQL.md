GCP Deployment Pipeline for Spring Boot WebFlux + React App with PostgreSQL
Here's a complete CI/CD pipeline setup to deploy your reactive Spring Boot (Maven) + React application to Google Cloud Platform (GCP) using Docker and Kubernetes (GKE).

Prerequisites
Google Cloud account with billing enabled

GKE cluster configured

Cloud SQL PostgreSQL instance

Google Cloud domain (espan360.com) configured

gcloud CLI installed and configured

GitHub repository access

1. Infrastructure as Code (Terraform)
infra/gke-cluster.tf
terraform
provider "google" {
  project = "your-gcp-project-id"
  region  = "us-central1"
}

resource "google_container_cluster" "espan360" {
  name     = "espan360-cluster"
  location = "us-central1"

  remove_default_node_pool = true
  initial_node_count       = 1

  network    = google_compute_network.espan360_vpc.name
  subnetwork = google_compute_subnetwork.espan360_subnet.name
}

resource "google_container_node_pool" "primary_nodes" {
  name       = "espan360-node-pool"
  location   = "us-central1"
  cluster    = google_container_cluster.espan360.name
  node_count = 2

  node_config {
    machine_type = "e2-medium"
    disk_size_gb = 100

    oauth_scopes = [
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }
}
infra/cloudsql.tf
terraform
resource "google_sql_database_instance" "postgres" {
  name             = "espan360-postgres"
  database_version = "POSTGRES_13"
  region           = "us-central1"

  settings {
    tier = "db-f1-micro"
    ip_configuration {
      ipv4_enabled = false
      private_network = google_compute_network.espan360_vpc.id
    }
  }

  deletion_protection = false
}

resource "google_sql_database" "database" {
  name     = "espan360db"
  instance = google_sql_database_instance.postgres.name
}

resource "google_sql_user" "users" {
  name     = "espanadmin"
  instance = google_sql_database_instance.postgres.name
  password = var.db_password
}
2. Docker Setup
Dockerfile (for Spring Boot backend)
dockerfile
# Build stage
FROM maven:3.8.6-eclipse-temurin-17 as builder
WORKDIR /workspace/app

COPY pom.xml .
COPY src src

RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /workspace/app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
Dockerfile (for React frontend)
dockerfile
# Build stage
FROM node:18 as builder
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# Run stage
FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
nginx.conf (same as before)
3. Kubernetes Manifests
k8s/backend-deployment.yaml
yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: gcr.io/your-project-id/backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://<CLOUDSQL_PRIVATE_IP>:5432/espan360db
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
---
apiVersion: v1
kind: Service
metadata:
  name: backend-service
spec:
  selector:
    app: backend
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
k8s/frontend-deployment.yaml
yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: frontend
        image: gcr.io/your-project-id/frontend:latest
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: frontend-service
spec:
  type: LoadBalancer
  selector:
    app: frontend
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
k8s/ingress.yaml
yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: espan360-ingress
  annotations:
    kubernetes.io/ingress.class: "gce"
    networking.gke.io/managed-certificates: "espan360-certificate"
spec:
  rules:
  - host: "espan360.com"
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend-service
            port:
              number: 80
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: backend-service
            port:
              number: 8080
4. GitHub Actions CI/CD Pipeline
.github/workflows/deploy.yml
yaml
name: Deploy to GCP GKE

on:
  push:
    branches: [ main ]

env:
  GCP_PROJECT: your-project-id
  GKE_CLUSTER: espan360-cluster
  GKE_ZONE: us-central1
  GCR_REPOSITORY: gcr.io/${{ env.GCP_PROJECT }}

jobs:
  setup-build-publish-deploy:
    name: Setup, Build, Publish, and Deploy
    runs-on: ubuntu-latest

    steps:
    - name: Checkout
      uses: actions/checkout@v3

    # Configure gcloud CLI
    - name: Authenticate to Google Cloud
      uses: google-github-actions/auth@v1
      with:
        credentials_json: ${{ secrets.GCP_CREDENTIALS }}

    - name: Set up Cloud SDK
      uses: google-github-actions/setup-gcloud@v1

    - name: Configure Docker
      run: gcloud auth configure-docker

    # Build and push backend
    - name: Build backend Docker image
      run: docker build -t $GCR_REPOSITORY/backend:latest -f Dockerfile.backend .
      
    - name: Push backend Docker image
      run: docker push $GCR_REPOSITORY/backend:latest

    # Build and push frontend
    - name: Build frontend Docker image
      run: docker build -t $GCR_REPOSITORY/frontend:latest -f Dockerfile.frontend .
      
    - name: Push frontend Docker image
      run: docker push $GCR_REPOSITORY/frontend:latest

    # Configure kubectl
    - name: Get GKE credentials
      run: gcloud container clusters get-credentials $GKE_CLUSTER --zone $GKE_ZONE --project $GCP_PROJECT

    # Deploy to Kubernetes
    - name: Deploy backend
      run: |
        kubectl apply -f k8s/backend-deployment.yaml
        kubectl apply -f k8s/backend-service.yaml

    - name: Deploy frontend
      run: |
        kubectl apply -f k8s/frontend-deployment.yaml
        kubectl apply -f k8s/frontend-service.yaml

    - name: Deploy ingress
      run: kubectl apply -f k8s/ingress.yaml

    - name: Verify deployment
      run: kubectl get pods,svc,ingress
5. Setup Instructions
Initialize Infrastructure:

bash
cd infra
terraform init
terraform apply
Configure GitHub Secrets:

GCP_CREDENTIALS - Service account JSON key with GKE and GCR permissions

DB_PASSWORD - PostgreSQL database password

Create Kubernetes Secrets for DB:

bash
kubectl create secret generic db-secret \
  --from-literal=username=espanadmin \
  --from-literal=password='your_db_password'
Configure Cloud SQL Proxy (if needed):

bash
kubectl create secret generic cloudsql-instance-credentials \
  --from-file=credentials.json=/path/to/service-account.json
Configure SSL Certificate:

bash
gcloud compute ssl-certificates create espan360-certificate \
  --domains espan360.com \
  --global
Push to GitHub:

bash
git add .
git commit -m "Initial GCP deployment setup"
git push origin main
6. Post-Deployment Steps
Configure DNS:

Get the external IP from the ingress:

bash
kubectl get ingress espan360-ingress -o jsonpath='{.status.loadBalancer.ingress[0].ip}'
Create an A record in your DNS provider for espan360.com pointing to this IP

Verify SSL Certificate:

Check that the managed certificate is active:

bash
kubectl get managedcertificate
This pipeline will automatically build, push Docker images to Google Container Registry (GCR), 
and deploy to GKE whenever code is pushed to the main branch. The application will be accessible at https://espan360.com.