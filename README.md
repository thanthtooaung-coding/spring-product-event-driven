# Spring Boot Event-Driven Product Service with RabbitMQ

This project is a demonstration of an event-driven microservice built with Spring Boot and RabbitMQ. It exposes a REST API to create a "Product" and then publishes a `ProductCreatedEvent` to a RabbitMQ exchange. A separate listener component consumes this event asynchronously to simulate a downstream process, such as updating a search index or notifying other services.

## Features

  - **REST API**: Simple, non-blocking API for creating products.
  - **Event-Driven**: Core business logic is decoupled through asynchronous events.
  - **RabbitMQ Integration**: Uses Spring AMQP for seamless integration with RabbitMQ.
  - **Decoupled Components**: The service that creates the product (Publisher) is completely unaware of the services that react to its creation (Consumers).
  - **Declarative Setup**: The RabbitMQ exchange, queue, and binding are all declared programmatically using Spring beans.
  - **JSON Payloads**: Events are serialized to JSON for language-agnostic interoperability.

## Architecture Overview

The application follows a simple publisher-subscriber pattern.

```
 [Client]
    |
    | (1) HTTP POST /api/products
    v
+------------------------ Product Service ------------------------+
|                                                                 |
|  [ProductController] -> [ProductService]                        |
|                            |                                    |
|                            | (2) Publishes `ProductCreatedEvent`|
|                            v                                    |
|                      [RabbitTemplate]                           |
|                                                                 |
+-----------------------------------------------------------------+
    |
    | (3) Message sent to Exchange
    v
+---------------------- RabbitMQ Broker ----------------------+
|                                                             |
|  [products-exchange] --(product.created)--> [products-queue]|
|                                                             |
+-------------------------------------------------------------+
    |
    | (4) Message pushed to Consumer
    v
+---------------------- Consumer Service ---------------------+
|                                                             |
|                      [NotificationListener]                 |
|                            |                                |
|                            | (5) Processes event            |
|                            v                                |
|                        [Business Logic]                     |
|                   (e.g., send notification)                 |
+-------------------------------------------------------------+

```

## Prerequisites

Before you begin, ensure you have the following installed:

  * **Java JDK 17** or later
  * **Apache Maven 3.8** or later
  * **Docker** (The easiest way to run a local RabbitMQ instance)

## Getting Started

Follow these steps to get the application up and running.

### 1\. Clone the Repository

```bash
git clone [https://github.com/thanthtooaung-coding/spring-product-event-driven](https://github.com/thanthtooaung-coding/spring-product-event-driven)
cd spring-product-event-driven
```

### 2\. Start RabbitMQ

Run a RabbitMQ instance using Docker. This command will start a container with the management plugin enabled.

```bash
docker run -d --hostname my-rabbit --name product-service-rabbit \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

  - The application will connect to RabbitMQ on port `5672`.
  - You can access the RabbitMQ Management UI at `http://localhost:15672` (user: `guest`, password: `guest`).

### 3\. Configure the Application

The application configuration is located in `src/main/resources/application.properties`. The default settings are configured to connect to the local RabbitMQ instance from the Docker command above.

```properties
# RabbitMQ Connection Details
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# RabbitMQ Topology
app.rabbitmq.exchange=products-exchange
app.rabbitmq.queue=products-queue
app.rabbitmq.routingkey=product.created
```

### 4\. Run the Application

You can run the application using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`.

## How to Use

Once the application is running, you can create a new product by sending a `POST` request to the `/api/products` endpoint.

Use a tool like `curl` or Postman:

```bash
curl -X POST http://localhost:8080/api/products \
-H "Content-Type: application/json" \
-d '{
      "productName": "High-Performance SSD",
      "price": 129.99
    }'
```

### Expected Output

1.  **HTTP Response**:

    ```
    Product creation request received and event published!
    ```

2.  **Console Logs**: You will see logs from both the publisher and the consumer, confirming the end-to-end flow.

    *Log from the service publishing the event:*

    ```
    INFO --- [nio-8080-exec-1] c.p.e.service.ProductService       : Saving product to the database... (simulated)
    INFO --- [nio-8080-exec-1] c.p.e.service.ProductService       : Published ProductCreatedEvent with Product ID: 2a9b3c4d-....
    ```

    *Log from the listener consuming the event (appears a moment later):*

    ```
    INFO --- [ntContainer#0-1] c.p.e.l.NotificationListener      : Received event: ProductCreatedEvent(productId=2a9b3c4d-..., productName=High-Performance SSD, ...)
    INFO --- [ntContainer#0-1] c.p.e.l.NotificationListener      : Processing new product notification for Product ID: 2a9b3c4d-...
    INFO --- [ntContainer#0-1] c.p.e.l.NotificationListener      : Notification processed successfully for Product ID: 2a9b3c4d-...
    ```

## Project Structure

```
.
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── product
│       │           └── eventdriven
│       │               ├── config
│       │               │   └── RabbitMQConfig.java       # Defines Queue, Exchange, and Binding
│       │               ├── controller
│       │               │   └── ProductController.java    # REST API endpoint
│       │               ├── event
│       │               │   └── ProductCreatedEvent.java  # DTO for the event payload
│       │               ├── listener
│       │               │   └── NotificationListener.java # Asynchronous event consumer
│       │               ├── service
│       │               │   └── ProductService.java       # Business logic and event publisher
│       │               └── EventdrivenApplication.java   # Main application class
│       └── resources
│           └── application.properties                    # Application configuration
├── pom.xml                                               # Maven dependencies
└── README.md                                             # README
```
