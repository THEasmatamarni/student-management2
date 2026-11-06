terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# Credentials (access key, secret, session token) are read from
# ~/.aws/credentials or from AWS_* environment variables.
provider "aws" {
  region = var.region
}

# Latest Ubuntu 22.04 image published by Canonical
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
}

data "aws_vpc" "default" {
  default = true
}

resource "aws_security_group" "student_sg" {
  name        = "student-management-sg"
  description = "SSH, Kubernetes API and application access"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Application (Kubernetes NodePort)"
    from_port   = 30080
    to_port     = 30080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Kubernetes API (used by Jenkins kubectl)"
    from_port   = 6443
    to_port     = 6443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "student_k8s" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  key_name               = var.key_name
  vpc_security_group_ids = [aws_security_group.student_sg.id]

  root_block_device {
    volume_size = 20
  }

  # Runs once at first boot: installs k3s (lightweight Kubernetes),
  # then deploys the manifests from the k8s/ folder of the Git repo.
  user_data = <<-EOT
    #!/bin/bash
    set -e
    apt-get update -y
    apt-get install -y git curl
    curl -sfL https://get.k3s.io | sh -s - --write-kubeconfig-mode 644
    until k3s kubectl get nodes 2>/dev/null | grep -q " Ready"; do sleep 5; done
    git clone ${var.repo_url} /opt/app
    k3s kubectl apply -f /opt/app/${var.k8s_path}/
  EOT

  tags = {
    Name = "student-management-k8s"
  }
}