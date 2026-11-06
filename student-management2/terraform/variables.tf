variable "region" {
  description = "AWS region (Learner Lab: us-east-1 or us-west-2)"
  type        = string
  default     = "us-east-1"
}

variable "key_name" {
  description = "Existing key pair name (Learner Lab: vockey)"
  type        = string
  default     = "vockey"
}

variable "instance_type" {
  description = "Instance type (2 vCPU / 4 GB minimum for k3s + MySQL + Spring)"
  type        = string
  default     = "t3.medium"
}

variable "repo_url" {
  description = "GitHub repository URL containing the k8s/ folder"
  type        = string
  default     = "https://github.com/THEasmatamarni/student-management2.git"
}

variable "k8s_path" {
  description = "Path of the k8s folder inside the repository"
  type        = string
  default     = "k8s"
}