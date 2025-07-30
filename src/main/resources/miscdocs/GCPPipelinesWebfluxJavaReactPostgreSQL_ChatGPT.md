To deploy your Spring Boot Reactive WebFlux + React app with PostgreSQL to Google Cloud Platform (GCP) using Docker, Kubernetes (GKE), and GitHub Actions CI/CD, here is a complete setup:

✅ Overview
GitHub Repo: https://github.com/javaugi/reactive-algotrade-gcp-postgres

Domain: espan360.com

Backend: Spring Boot WebFlux (Reactive)

Frontend: React (in subdirectory or separate repo)

Database: PostgreSQL (Cloud SQL or containerized)

Deployment: GCP with GKE (Google Kubernetes Engine)

CI/CD: GitHub Actions

✅ Step-by-Step Pipeline & Scripts
1. 🌐 DNS: Point Domain to GCP Load Balancer (After Ingress is set up)
Update espan360.com A record in your domain provider to point to GCP's Ingress IP. You’ll get this after setting up the ingress controller.

2. 🔐 GitHub Secrets (in repo settings)
Key	Value
GCP_PROJECT	Your GCP project ID
GCP_REGION	e.g. us-central1
GKE_CLUSTER	Your cluster name
GKE_ZONE	e.g. us-central1-a
GCP_SA_KEY	Base64 of your service account JSON key (see below)
DOCKERHUB_USERNAME	Your Docker Hub username
DOCKERHUB_TOKEN	Docker Hub access token/password

Create a GCP service account with:

sh
Copy
Edit
gcloud iam service-accounts create github-cicd --display-name="GitHub CI/CD"
gcloud projects add-iam-policy-binding $GCP_PROJECT \
  --member="serviceAccount:github-cicd@$GCP_PROJECT.iam.gserviceaccount.com" \
  --role="roles/container.admin"
gcloud iam service-accounts keys create key.json \
  --iam-account=github-cicd@$GCP_PROJECT.iam.gserviceaccount.com
base64 key.json  # Copy and add to GitHub secret GCP_SA_KEY
3. 📁 Project Structure (simplified)
bash
Copy
Edit
/backend   # Spring Boot App (with Dockerfile)
/frontend  # React App (with Dockerfile)
/k8s       # Kubernetes manifests
/.github/workflows/deploy.yml
4. 🐳 Dockerfiles
backend/Dockerfile

dockerfile
Copy
Edit
FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
frontend/Dockerfile

dockerfile
Copy
Edit
FROM node:20 as builder
WORKDIR /app
COPY . .
RUN npm install && npm run build

FROM nginx:alpine
COPY --from=builder /app/build /usr/share/nginx/html
EXPOSE 80
5. 🧾 Kubernetes YAMLs (k8s/)
backend-deployment.yaml

yaml
Copy
Edit
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
        image: DOCKER_HUB_USER/backend:latest
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: backend
spec:
  selector:
    app: backend
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
frontend-deployment.yaml

yaml
Copy
Edit
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
        image: DOCKER_HUB_USER/frontend:latest
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: frontend
spec:
  selector:
    app: frontend
  ports:
    - protocol: TCP
      port: 80
      targetPort: 80
ingress.yaml

yaml
Copy
Edit
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: esp-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
    - host: espan360.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: frontend
                port:
                  number: 80
          - path: /api
            pathType: Prefix
            backend:
              service:
                name: backend
                port:
                  number: 8080
6. 🤖 GitHub Actions CI/CD .github/workflows/deploy.yml
yaml
Copy
Edit
name: Deploy to GCP GKE

on:
  push:
    branches: [ main ]

env:
  PROJECT_ID: ${{ secrets.GCP_PROJECT }}
  REGION: ${{ secrets.GCP_REGION }}
  CLUSTER: ${{ secrets.GKE_CLUSTER }}
  ZONE: ${{ secrets.GKE_ZONE }}

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout
      uses: actions/checkout@v3

    - name: Set up Docker
      uses: docker/setup-buildx-action@v2

    - name: Authenticate to GCP
      uses: google-github-actions/auth@v1
      with:
        credentials_json: ${{ secrets.GCP_SA_KEY }}

    - name: Configure Docker for GCP
      run: gcloud auth configure-docker

    - name: Configure GKE credentials
      run: |
        gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID

    - name: Build and Push Backend
      run: |
        docker build -t $DOCKER_HUB_USERNAME/backend:latest ./backend
        echo ${{ secrets.DOCKERHUB_TOKEN }} | docker login -u $DOCKER_HUB_USERNAME --password-stdin
        docker push $DOCKER_HUB_USERNAME/backend:latest

    - name: Build and Push Frontend
      run: |
        docker build -t $DOCKER_HUB_USERNAME/frontend:latest ./frontend
        docker push $DOCKER_HUB_USERNAME/frontend:latest

    - name: Deploy to GKE
      run: |
        kubectl apply -f k8s/backend-deployment.yaml
        kubectl apply -f k8s/frontend-deployment.yaml
        kubectl apply -f k8s/ingress.yaml
✅ Optional: PostgreSQL (Cloud SQL)
If you want to use Cloud SQL instead of containerized PostgreSQL:

Create a Cloud SQL instance with PostgreSQL.

Set up a private IP or use the Cloud SQL proxy sidecar.

Update application.yml in Spring Boot to use the Cloud SQL JDBC URL.

Add Kubernetes Secret and ConfigMap for credentials and DB host.

✅ Final Notes
💬 Make sure frontend proxy is configured correctly (e.g. proxy: "/api" in package.json).

🔐 Use HTTPS with a TLS certificate (you can auto-provision via cert-manager on GKE).

✅ Test locally using minikube or kind before deploying.

