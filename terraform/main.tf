terraform {
  required_providers {
    digitalocean = {
      source = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}

# Set the variable value in *.tfvars file
# or using -var="do_token=..." CLI option
variable "do_token" {}

# Configure the DigitalOcean Provider
provider "digitalocean" {
  token = var.do_token
}

resource "digitalocean_container_registry" "todos" {
  name                   = "todos"
  subscription_tier_slug = "basic"
  region                 = "nyc3"
}

data "digitalocean_kubernetes_versions" "todos" {
  version_prefix = "1.24."
}

resource "digitalocean_kubernetes_cluster" "todos" {
  name         = "todos"
  region       = "nyc3"
  auto_upgrade = true
  version      = data.digitalocean_kubernetes_versions.todos.latest_version

  maintenance_policy {
    start_time  = "04:00"
    day         = "sunday"
  }

  node_pool {
    name       = "default"
    size       = "s-2vcpu-4gb"
    node_count = 1
    auto_scale = false
  }
}

resource "digitalocean_database_cluster" "todos" {
  name       = "todos"
  engine     = "pg"
  version    = "14"
  size       = "db-s-1vcpu-1gb"
  region     = "nyc3"
  node_count = 1
}

resource "digitalocean_database_connection_pool" "pool-01" {
  cluster_id = digitalocean_database_cluster.todos.id
  name       = "pool-01"
  mode       = "transaction"
  size       = 20
  db_name    = "defaultdb"
  user       = "doadmin"
}