terraform {
  backend "s3" {
    bucket       = "gokul-terraform-state-546068156511"
    key          = "fraud-service/terraform.tfstate"
    region       = "us-east-1"
    encrypt      = true
    use_lockfile = true
  }
}