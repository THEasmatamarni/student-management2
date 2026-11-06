variable "region" {
  description = "AWS region (Learner Lab: us-east-1 or us-west-2)"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "EKS cluster name"
  type        = string
  default     = "student-eks"
}

variable "availability_zones" {
  description = "AZs supported by EKS (us-east-1e is NOT supported)"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b", "us-east-1c"]
}

variable "node_instance_type" {
  description = "Worker node instance type"
  type        = string
  default     = "t3.medium"
}

variable "node_count" {
  description = "Number of worker nodes"
  type        = number
  default     = 2
}