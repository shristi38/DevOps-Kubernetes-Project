# DevOps Kubernetes CI/CD Project

## Project Overview

This project demonstrates an end-to-end CI/CD pipeline for deploying a Spring Boot application using Docker and Kubernetes.

The project extends the previous Docker-based CI/CD project by introducing Kubernetes for container orchestration and AWS EKS as the cloud Kubernetes environment.

### CI/CD Flow

Developer → GitHub → Jenkins → Maven Build/Test → Docker Build → Docker Hub → Kubernetes → Application

The Kubernetes deployment can be tested locally using Minikube and in the cloud using Amazon EKS.

---

## Technologies Used

- Git
- GitHub
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

The developer pushes the latest application changes to GitHub.

```bash
git add .
git commit -m "Update application"
git push origin main
```

## 2. Jenkins

Jenkins pulls the source code from GitHub and starts the CI/CD pipeline.

Pipeline stages:

```text
Build
   ↓
Docker Build
   ↓
Docker Push
   ↓
Kubernetes Deployment
```

## 3. Maven Build and Test

Jenkins executes:

```bash
mvn clean package
```

This performs:

- Clean previous build files
- Compile the Java application
- Run unit tests
- Generate the Spring Boot JAR

## 4. Docker Build

Jenkins creates the Docker image:

```bash
docker build -t devops-cicd-java-app .
```

## 5. Docker Hub

The Docker image is tagged and pushed to Docker Hub.

Example:

```text
shrishti38/devops-cicd-java-app:v3
```

Docker Hub acts as the container image registry.

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

## Service

File:

```text
k8s/service.yaml
```

The Service provides network access to the application Pods.

Service type:

```text
NodePort
```

Application container port:

```text
8080
```

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

Minikube was used to test the Kubernetes deployment locally.

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

Access the application:

```bash
minikube service spring-boot-app-service -n devops-k8s --url
```

---

# AWS EKS - Cloud Kubernetes

The project is extended to AWS EKS for cloud-based Kubernetes deployment.

## EKS Configuration

```text
Cluster Name: devops-eks-cluster
Region: us-east-2
Kubernetes Version: 1.34
Worker Nodes: 2
Instance Type: t3.small
```

The cluster is created using `eksctl`.

Example:

```bash
eksctl create cluster \
  --name devops-eks-cluster \
  --region us-east-2 \
  --nodes 2 \
  --node-type t3.small
```

After the EKS cluster is available, the Kubernetes manifests can be deployed to the EKS cluster.

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
```

Kubernetes resources are created or updated using:

```bash
kubectl apply
```

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

# Docker

The Spring Boot application is packaged into a Docker image.

Application port:

```text
8080
```

Docker image:

```text
shrishti38/devops-cicd-java-app:v3
```

Kubernetes pulls this image from Docker Hub to create the application Pods.

---

# Security

The project demonstrates basic security practices:

- Docker Hub credentials are stored in Jenkins Credentials.
- Kubernetes Secrets are separated from normal configuration.
- AWS credentials should never be committed to GitHub.
- Real passwords should not be stored directly in source code.
- AWS IAM permissions should follow the principle of least privilege.

---

# DevOps Concepts Demonstrated

- Git and GitHub
- Jenkins CI/CD
- Maven
- Unit Testing
- Docker
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
    | Jenkins Pipeline
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
       Kubernetes Cluster
        /             \
       /               \
  Minikube            AWS EKS
   Local               Cloud
       \               /
        \             /
         Spring Boot
         Application
```

---

# Project Objective

The objective of this project is to demonstrate how a Spring Boot application can be automatically built, tested, containerized, pushed to a container registry, and deployed to Kubernetes.

The project also demonstrates the transition from local Kubernetes using Minikube to cloud Kubernetes using Amazon EKS.

---

# Future Improvements

- Jenkins deployment directly to EKS
- AWS IAM-based Jenkins-to-EKS authentication
- Terraform-based AWS infrastructure
- Automated Docker image versioning
- Kubernetes rolling updates
- Deployment rollback
- CPU and memory resource requests/limits
- Horizontal Pod Autoscaling
- Kubernetes Ingress
- HTTPS
- Monitoring and logging

---

## Author

**Shrishti Tripathi**

DevOps Learning Project - Kubernetes CI/CD
