# 🛒 Order Service - E-Commerce Microservices

![Java](https://img.shields.io/badge/Java-17+-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-orange)
![Kafka](https://img.shields.io/badge/Kafka-Event%20Driven-black)

## 📖 Overview
The **Order Service** is a core microservice in our distributed e-commerce architecture. It is responsible for handling user order placements, verifying product availability, and seamlessly falling back during infrastructure outages. 

This service demonstrates robust microservice design patterns, including **Service Discovery** and **Fault Tolerance**, ensuring high availability even when downstream services (like the Inventory Service) experience downtime.

## 🏗️ Architecture & Key Patterns

### 1. Service Discovery (Netflix Eureka) 🔍
Hardcoding IP addresses is avoided. The Order Service integrates with a central **Eureka Server** to dynamically discover the `inventory-service`. Traffic is client-side load-balanced, allowing seamless scaling of the inventory instances.

### 2. Circuit Breaker (Resilience4j) 🛡️
Network unreliability is handled gracefully using **Resilience4j**. 
- If the `inventory-service` fails repeatedly (e.g., >50% failure rate), the circuit **opens**, halting requests to prevent cascading failures.
- A **Fallback Method** immediately takes over, returning a meaningful response ("We are currently having trouble reaching our inventory system") instead of a generic HTTP 500 error.
- The circuit transitions to **half-open** after a set duration to automatically test if the downstream service has recovered.

### 3. Event-Driven Messaging (Apache Kafka) 📢
Post-order operations are decoupled. Once an order is successfully persisted, the service pushes an `order-created` event to a **Kafka topic**. This allows independent services (like Notifications or Shipping) to react without slowing down the initial user request.

---

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven** (for building the project)
- **Apache Kafka** (running locally or via Docker)
- **Eureka Server** (running on port `8761`)
- **Inventory Service** (running and registered with Eureka)

### Installation & Execution

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/order-service.git
   cd order-service
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   *The service will start and register itself with Eureka automatically.*

---

## 🛠️ Configuration Highlights (`application.yml`)

The Circuit Breaker is tuned to prevent "flapping" while providing quick recovery:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      inventoryService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
```

## 🌐 API Endpoints

### Place an Order
- **URL**: `/api/orders`
- **Method**: `POST`
- **Payload Example**:
  ```json
  {
    "skuCode": "iphone_15",
    "price": 999.00,
    "quantity": 1
  }
  ```
- **Responses**:
  - `201 Created`: Order successfully placed.
  - `500 Internal Server Error`: Thrown securely by the fallback method if inventory is unreachable.

---
*Built with ❤️ for scalable architecture.*
