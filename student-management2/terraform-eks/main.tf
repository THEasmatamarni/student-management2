terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.region
}

# Learner Lab: IAM roles cannot be created, so reuse the existing LabRole
data "aws_iam_role" "lab" {
  name = "LabRole"
}

data "aws_vpc" "default" {
  default = true
}

# Only subnets in AZs supported by EKS
data "aws_subnets" "eks" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
  filter {
    name   = "availability-zone"
    values = var.availability_zones
  }
}

resource "aws_eks_cluster" "this" {
  name     = var.cluster_name
  role_arn = data.aws_iam_role.lab.arn

  vpc_config {
    subnet_ids = data.aws_subnets.eks.ids
  }

  # The IAM identity creating the cluster becomes cluster admin.
  # Jenkins uses the same Learner Lab credentials, so it gets admin too.
  access_config {
    authentication_mode                         = "API_AND_CONFIG_MAP"
    bootstrap_cluster_creator_admin_permissions = true
  }
}

resource "aws_eks_node_group" "this" {
  cluster_name    = aws_eks_cluster.this.name
  node_group_name = "${var.cluster_name}-nodes"
  node_role_arn   = data.aws_iam_role.lab.arn
  subnet_ids      = data.aws_subnets.eks.ids
  instance_types  = [var.node_instance_type]
  ami_type        = "BOTTLEROCKET_x86_64"

  scaling_config {
    desired_size = var.node_count
    min_size     = 1
    max_size     = var.node_count
  }
}
