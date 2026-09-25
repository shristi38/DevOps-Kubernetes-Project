# DevOps CI/CD Project

## Project Overview

This project demonstrates an end-to-end CI/CD pipeline for a Spring Boot application using Jenkins, Maven, Docker, Docker Hub, GitHub, and AWS EC2.

The pipeline automatically builds, tests, containerizes, and deploys the application whenever code is pushed to the main branch.

## Architecture

Developer
    |
    | git push
    ↓
GitHub Repository
    |
    | Webhook
    ↓
Jenkins on AWS EC2
    |
    ├── Maven Build
    ├── Unit Tests
    ├── Docker Build
    └── Docker Push
            |
            ↓
       Docker Hub
            |
            ↓
       Docker Pull
            |
            ↓
    Docker Container
       on AWS EC2
            |
            ↓
    Spring Boot Application
            |
            ↓
       Browser :8081

## Technologies Used

- Git
- GitHub
- Jenkins
- Maven
- Java
- Spring Boot
- Docker
- Docker Hub
- AWS EC2
- Linux
- GitHub Webhooks

## Project Structure

```text
Devops-CICD-Project/
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
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```

## CI/CD Pipeline

### 1. Code Commit

Developer pushes application changes to the GitHub `main` branch.

### 2. GitHub Webhook

GitHub webhook automatically triggers the Jenkins pipeline whenever new code is pushed.

### 3. Maven Build

Jenkins executes:

```bash
mvn clean package
```

This compiles the application, runs unit tests, and generates the JAR file.

### 4. Docker Build

Jenkins creates a Docker image from the application:

```bash
docker build -t devops-cicd-java-app .
```

### 5. Docker Hub Push

The Docker image is tagged and pushed to Docker Hub.

### 6. Deployment to AWS EC2

Jenkins pulls the latest Docker image from Docker Hub, stops the previous application container, removes it, and starts a new container.

The application is exposed through port `8081` on the EC2 instance.

### 7. Application Access

The Spring Boot application can be accessed through:

```text
http://<EC2-PUBLIC-IP>:8081
```

## Jenkins Pipeline Stages

The Jenkins pipeline contains the following stages:

- Build
- Docker Build
- Docker Push
- Deploy

## Docker

The application runs inside a Docker container.

Container port:

```text
8080
```

EC2 exposed port:

```text
8081
```

Port mapping:

```text
EC2:8081 → Container:8080
```

## Troubleshooting

### Jenkins Node Offline

Jenkins initially reported insufficient temporary disk space because `/tmp` was a small `tmpfs` filesystem.

The Jenkins temporary disk monitoring threshold was adjusted so that the Jenkins node could come online.

### Maven Not Found

The Jenkins build initially failed because Maven was not installed on the EC2 instance.

Maven was installed and the pipeline was successfully executed afterward.

### Docker Permission Issue

Docker commands executed by the Jenkins user initially required proper Docker group permissions.

The Jenkins user was added to the Docker group so Jenkins could execute Docker commands.

### Port 8080 Already in Use

Port `8080` was already being used by Jenkins on the EC2 instance.

Therefore, the application was exposed using:

```text
8081:8080
```

This means EC2 port `8081` forwards traffic to application port `8080` inside the container.

### AWS Security Group

Inbound access for TCP port `8081` was configured in the EC2 Security Group so the application could be accessed externally.

## Key DevOps Concepts Demonstrated

- Continuous Integration
- Continuous Deployment
- Jenkins Pipeline
- GitHub Webhooks
- Maven Build Automation
- Unit Testing
- Docker Containerization
- Docker Registry
- AWS EC2 Deployment
- Linux Administration
- CI/CD Troubleshooting

## Future Improvements

Possible improvements for this project include:

- Docker image versioning
- Application health checks
- Deployment rollback
- HTTPS using a reverse proxy
- Infrastructure as Code using Terraform
- Kubernetes deployment
- Monitoring and logging
