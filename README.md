# DevOps Kubernetes CI/CD Project

## Project Overview

This project demonstrates an end-to-end CI/CD pipeline for deploying a Spring Boot application using Docker, Kubernetes, Jenkins, and AWS EKS.

The project extends the previous Docker-based CI/CD project by introducing Kubernetes for container orchestration and Amazon EKS as the cloud Kubernetes environment.

### CI/CD Flow

Developer → GitHub → GitHub Webhook → Jenkins → Maven Build/Test → Docker Build → Docker Hub → AWS EKS → LoadBalancer → Application

The Kubernetes deployment was first tested locally using Minikube and was then deployed to AWS EKS.

---

## Technologies Used

- Git
- GitHub
- GitHub Webhooks
- Jenkins
- Maven
- Java 17
- Spring Boot
- Docker
- Docker Hub
- Kubernetes
- Minikube
- Amazon EKS
- AWS EC2
- AWS IAM
- AWS Elastic Load Balancing
- Linux
- YAML

---

## Project Structure

```text
DevOps-Kubernetes-Project/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── devops/
│   │               └── App.java
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── devops/
│                   └── AppTest.java
│
├── k8s/
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── deployment.yaml
│   └── service.yaml
│
├── Dockerfile
├── Jenkinsfile
├── pom.xml
├── .gitignore
└── README.md
```

---

# CI/CD Pipeline

## 1. Developer Pushes Code

The developer pushes application changes to GitHub.

```bash
git add .
git commit -m "Update application"
git push origin main
```

## 2. GitHub Webhook

A GitHub webhook automatically triggers the Jenkins pipeline whenever code is pushed to the `main` branch.

```text
Developer Push
      ↓
GitHub
      ↓
GitHub Webhook
      ↓
Jenkins
```

## 3. Jenkins

Jenkins pulls the latest source code and executes the CI/CD pipeline.

Pipeline stages:

```text
Build
  ↓
Docker Build
  ↓
Docker Push
  ↓
Deploy to Kubernetes
```

## 4. Maven Build and Test

Jenkins executes:

```bash
mvn clean package
```

This performs:

- Clean previous build files
- Compile the Java application
- Run unit tests
- Generate the Spring Boot JAR

## 5. Docker Build

Jenkins creates the Docker image.

For AWS EKS, the image was built specifically for the EKS worker-node architecture:

```bash
docker buildx build --platform linux/amd64 -t shrishti38/devops-cicd-java-app:v4 --push .
```

Docker image:

```text
shrishti38/devops-cicd-java-app:v4
```

## 6. Docker Hub

The Docker image is pushed to Docker Hub and later pulled by the EKS worker nodes.

---

# Kubernetes Deployment

The application is deployed using Kubernetes manifests.

## Namespace

The application runs in:

```text
devops-k8s
```

## Deployment

File:

```text
k8s/deployment.yaml
```

The Deployment manages the Spring Boot application Pods.

Current configuration:

```yaml
replicas: 5
```

The Deployment uses:

```text
shrishti38/devops-cicd-java-app:v4
```

## Service

File:

```text
k8s/service.yaml
```

The Service exposes the application using:

```text
type: LoadBalancer
```

The application container listens on port:

```text
8080
```

The Kubernetes Service exposes port:

```text
80
```

AWS automatically provisions a Load Balancer for the Service when deployed on EKS.

## ConfigMap

File:

```text
k8s/configmap.yaml
```

The ConfigMap stores non-sensitive configuration.

Example:

```text
APP_ENV=development
```

## Secret

File:

```text
k8s/secret.yaml
```

The Secret stores sensitive configuration separately from normal application configuration.

Example:

```text
DB_PASSWORD
```

> Base64 is encoding, not encryption.

---

# Minikube - Local Kubernetes

Minikube was used initially to test the Kubernetes deployment locally.

Start Minikube:

```bash
minikube start --driver=docker
```

Create the namespace:

```bash
kubectl create namespace devops-k8s
```

Deploy the Kubernetes resources:

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Check Pods:

```bash
kubectl get pods -n devops-k8s
```

Check Deployment:

```bash
kubectl get deployment -n devops-k8s
```

Check Service:

```bash
kubectl get svc -n devops-k8s
```

Access the application locally:

```bash
minikube service spring-boot-app-service -n devops-k8s --url
```

---

# AWS EKS - Cloud Kubernetes

The application is deployed to Amazon EKS for cloud-based Kubernetes orchestration.

## EKS Configuration

```text
Cluster Name: devops-eks-cluster
Region: us-east-2
Kubernetes Version: 1.34
Worker Nodes: 2
Instance Type: t3.small
Namespace: devops-k8s
```

The cluster was created using `eksctl`:

```bash
eksctl create cluster \
  --name devops-eks-cluster \
  --region us-east-2 \
  --nodes 2 \
  --node-type t3.small
```

Verify worker nodes:

```bash
kubectl get nodes
```

Both worker nodes were verified in `Ready` state.

---

# Jenkins to EKS Authentication

Jenkins runs on an AWS EC2 instance.

Instead of storing AWS access keys on Jenkins, an IAM role was attached to the Jenkins EC2 instance.

```text
Jenkins EC2
    ↓
IAM Role: JenkinsEKSRole
    ↓
AWS EKS Access Entry
    ↓
EKS Cluster
```

The Jenkins IAM role is used to authenticate to AWS and access the EKS cluster.

The Jenkins user was configured with an EKS kubeconfig using:

```bash
aws eks update-kubeconfig --region us-east-2 --name devops-eks-cluster
```

Jenkins can then execute normal Kubernetes commands:

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

---

# Kubernetes Health Probes

The application Deployment uses Kubernetes health checks.

## Startup Probe

Checks whether the application has successfully started.

## Liveness Probe

Checks whether the application is still running.

## Readiness Probe

Checks whether the application is ready to receive traffic.

Example:

```yaml
startupProbe:
  httpGet:
    path: /
    port: 8080
```

---

# AWS Load Balancer

The Kubernetes Service uses the `LoadBalancer` type:

```yaml
type: LoadBalancer
```

AWS provisions an external Load Balancer for the application.

Traffic flow:

```text
Internet
   ↓
AWS Load Balancer
   ↓
Kubernetes Service :80
   ↓
Spring Boot Pods :8080
```

The application was successfully tested through the AWS Load Balancer.

---

# Jenkins Kubernetes Deployment

The Jenkins pipeline deploys the following Kubernetes resources:

```text
ConfigMap
    ↓
Secret
    ↓
Deployment
    ↓
Service
    ↓
AWS Load Balancer
```

Kubernetes resources are created or updated using:

```bash
kubectl apply
```

The deployment stage in Jenkins uses the EKS kubeconfig directly and no longer depends on the previous Minikube reverse SSH tunnel.

---

# Kubernetes Troubleshooting

Important commands used during the project:

### Check Pods

```bash
kubectl get pods -n devops-k8s
```

### Check Pod Details

```bash
kubectl describe pod <pod-name> -n devops-k8s
```

### Check Application Logs

```bash
kubectl logs <pod-name> -n devops-k8s
```

### Check Services

```bash
kubectl get svc -n devops-k8s
```

### Check Deployments

```bash
kubectl get deployment -n devops-k8s
```

### Check Events

```bash
kubectl get events -n devops-k8s
```

These commands are useful for troubleshooting:

- CrashLoopBackOff
- ImagePullBackOff
- Pending Pods
- Failed health probes
- Container startup failures
- Service connectivity issues

---

# Issue Resolved During EKS Deployment

The first Docker image produced an EKS error:

```text
no match for platform in manifest
```

The image was rebuilt for the EKS worker-node platform using:

```bash
docker buildx build --platform linux/amd64 \
  -t shrishti38/devops-cicd-java-app:v4 \
  --push .
```

The Kubernetes Deployment was then updated to use `v4`.

This demonstrates an important real-world troubleshooting scenario involving container image architecture compatibility.

---

# Docker

The Spring Boot application is packaged into a Docker image.

Application port:

```text
8080
```

Docker image:

```text
shrishti38/devops-cicd-java-app:v4
```

Kubernetes pulls this image from Docker Hub to create the application Pods.

---

# Security

The project demonstrates basic security practices:

- Docker Hub credentials are stored in Jenkins Credentials.
- Kubernetes Secrets are separated from normal configuration.
- AWS credentials are not stored directly on Jenkins.
- Jenkins uses an EC2 IAM role for AWS authentication.
- AWS IAM permissions should follow the principle of least privilege.
- Real passwords should not be committed to GitHub.

---

# DevOps Concepts Demonstrated

- Git and GitHub
- GitHub Webhooks
- Jenkins CI/CD
- Maven
- Unit Testing
- Docker
- Docker Buildx
- Docker Hub
- Kubernetes
- Pods
- Deployments
- ReplicaSets
- Services
- ConfigMaps
- Secrets
- Namespaces
- Startup Probes
- Liveness Probes
- Readiness Probes
- Minikube
- Amazon EKS
- AWS EC2
- AWS IAM
- EKS Access Entries
- AWS Load Balancer
- Linux
- Container orchestration
- CI/CD troubleshooting

---

# End-to-End Architecture

```text
Developer
    |
    | Git Push
    ↓
GitHub
    |
    | Webhook
    ↓
Jenkins on AWS EC2
    |
    ├── Maven Build
    ├── Unit Test
    ├── Docker Build
    └── Docker Push
             |
             ↓
        Docker Hub
             |
             ↓
          AWS EKS
             |
       Kubernetes Cluster
             |
       ┌─────┴─────┐
       ↓           ↓
 Deployment     Service
   5 Pods       LoadBalancer
                    |
                    ↓
              AWS Load Balancer
                    |
                    ↓
             Spring Boot App
```

---

# Project Objective

The objective of this project is to demonstrate how a Spring Boot application can be automatically built, tested, containerized, pushed to a container registry, and deployed to Kubernetes using Jenkins.

The project also demonstrates the transition from local Kubernetes using Minikube to cloud Kubernetes using Amazon EKS, including IAM-based Jenkins authentication and an AWS Load Balancer for external application access.

---

# Future Improvements

- Terraform-based AWS infrastructure
- Automated Docker image versioning
- Kubernetes rolling updates
- Deployment rollback
- CPU and memory resource requests/limits
- Horizontal Pod Autoscaling
- Kubernetes Ingress
- HTTPS
- Prometheus and Grafana monitoring
- Centralized logging
- CloudWatch integration

---

## Author

**Shrishti Tripathi**

DevOps Learning Project - Kubernetes CI/CD
