resource "aws_ecr_repository" "fraud_service" {
  name                 = "fraud-service"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Application = "fraud-service"
  }
}

output "ecr_repository_url" {
  value = aws_ecr_repository.fraud_service.repository_url
}