#infra/cloudsql.tf
#terraform
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