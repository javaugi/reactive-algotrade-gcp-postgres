#1. Infrastructure as Code (Terraform)
#infra/gke-cluster.tf
#terraform

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