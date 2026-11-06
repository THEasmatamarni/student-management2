output "public_ip" {
  description = "Public IP of the instance"
  value       = aws_instance.student_k8s.public_ip
}

output "app_url" {
  description = "Swagger URL (available 3 to 5 minutes after apply)"
  value       = "http://${aws_instance.student_k8s.public_ip}:30080/student/swagger-ui/index.html"
}

output "ssh_command" {
  description = "SSH command"
  value       = "ssh -i labsuser.pem ubuntu@${aws_instance.student_k8s.public_ip}"
}