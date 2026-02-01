# NutriTrack – CI/CD Microservices Project

NutriTrack is a microservices-based digital health platform focused on modern diets and lifestyle diseases. 
It allows users to track nutrition (meals, calories, macronutrients) and lifestyle metrics (sleep, hydration, activity) 
to better understand their long-term health risks.

The system is built using Spring Boot and Spring Cloud, with multiple microservices for configuration, discovery, gateway routing, 
and domain-specific logic (lifestyle and nutrition).

This repository is used for the Microservices Architecture and Continuous Build & Delivery modules.

## Build Instructions

### Prerequisites
- Java 17+
- Maven 3.8+
- Git
- Docker (for container builds)
- Minikube (for local Kubernetes deployment – optional at this stage)

### How to Build

From the project root:

```bash
mvn clean install

This command will build all microservice modules:

config-service
discovery-service
api-gateway
lifestyle-service
nutrition-service


*(In Word, you’ll reformat if needed; here it’s Markdown for GitHub.)*

### 3.3. Note That Kubernetes + Minikube Will Be Used

Add a short deployment note:

```markdown
## Deployment Approach

In later stages of the project, NutriTrack will be deployed to a local Kubernetes cluster 
(using **Minikube**) rather than only using Docker Compose.

The planned workflow is:

1. Build Docker images for each microservice.
2. Push images to Docker Hub (private or team namespace).
3. Deploy the services to Minikube using Kubernetes manifests (Deployments and Services).
4. Expose the API Gateway for external access (e.g., via NodePort or Ingress).

This setup is intended to demonstrate a cloud-native CI/CD pipeline and microservices deployment model.

