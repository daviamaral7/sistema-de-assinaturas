# SaaS Subscription System
![Java](https://img.shields.io/badge/Java-25-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Tests](https://img.shields.io/badge/Tests-JUnit%205-brightgreen)

A backend API for managing SaaS customers, subscription plans, billing invoices, payments, and project limits. The project models a subscription-based product where customers subscribe to plans, receive invoices, handles billing and payments, and can create projects according to the limits defined by their active plan.

This project was built as a portfolio backend application, with emphasis on business rules, domain modeling, persistence consistency, and a clean service-oriented architecture using Spring Boot.

## Key Features

- Customer management with lifecycle status control.
- Plan management with billing cycle and `maxProjects` configuration.
- Subscription lifecycle management with `ACTIVE`, `CANCELED`, and `PAST_DUE` states.
- Invoice generation based on subscription billing dates.
- Duplicate invoice prevention for the same subscription period.
- Overdue invoice handling with subscription status updates.
- Payment processing with amount validation.
- Project management constrained by the customer's active subscription plan.
- Database versioning with Flyway migrations.
- PostgreSQL and pgAdmin local environment with Docker Compose.

## System Architecture Overview

The system follows a layered Spring Boot architecture:

- **Controllers** expose REST endpoints and handle HTTP request/response concerns.
- **Services** contain business rules and coordinate repository operations.
- **Repositories** use Spring Data JPA for persistence.
- **DTOs** isolate API payloads from JPA entities.
- **Mappers** convert domain entities into API response models.
- **Flyway migrations** define and evolve the PostgreSQL schema.

### Domain Relationships

- A **Customer** represents a SaaS account owner.
- A **Plan** defines subscription pricing, billing cycle, and the maximum number of active projects allowed through `maxProjects`.
- A **Subscription** links a customer to a plan and tracks the subscription status and next billing date.
- An **Invoice** belongs to a subscription and represents a bill for a specific due date.
- A **Payment** belongs to an invoice and records a successful payment attempt.
- A **Project** belongs to a customer and can only be created or reactivated when the customer has an active subscription and has not reached the plan project limit.

In practice, the subscription is the central business link between customers, plans, invoices, payments, and project limits.

## Business Rules

### Subscription Rules

- A customer can have only one `ACTIVE` subscription at a time.
- A `BLOCKED` customer cannot create a new subscription.
- A subscription starts as `ACTIVE`.
- `nextBillingDate` is calculated from the selected plan billing cycle.
- Canceling a subscription changes its status to `CANCELED` and sets the end date.

### Invoice Rules

- Invoices can only be generated for `ACTIVE` subscriptions.
- The system prevents duplicate invoices for the same subscription and due date.
- Generated invoices start with status `OPEN`.
- When an invoice becomes overdue, the related subscription is moved to `PAST_DUE`.
- An invoice can only be marked overdue when:
  - its status is `OPEN`;
  - its due date is before the current date.

### Payment Rules

- Only `OPEN` or `OVERDUE` invoices can be paid.
- The payment amount must exactly match the invoice amount.
- After a successful payment:
  - the invoice status becomes `PAID`;
  - `paidAt` is set;
  - a payment record is created with status `APPROVED`.
- If the subscription has no more overdue invoices after payment, the subscription becomes `ACTIVE`.

### Project Rules

- A customer cannot create a project without an `ACTIVE` subscription.
- Project creation is limited by the active subscription plan's `maxProjects`.
- Reactivating an inactive project also respects the plan's `maxProjects` limit.
- Projects can be deactivated and later reactivated if the subscription rules allow it.

## API Overview

This section lists the main API endpoints. It is not intended to replace full API documentation.

### Customers

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/customers` | Create a customer |
| `GET` | `/customers` | List customers |
| `GET` | `/customers/{id}` | Get a customer by ID |
| `PUT` | `/customers/{id}` | Update customer data |
| `DELETE` | `/customers/{id}` | Soft-delete a customer |

### Plans

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/plans` | Create a subscription plan |
| `GET` | `/plans` | List active plans |
| `GET` | `/plans/{id}` | Get a plan by ID |
| `PUT` | `/plans/{id}` | Update a plan |
| `DELETE` | `/plans/{id}` | Deactivate a plan |

### Subscriptions

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/subscriptions` | Create a subscription |
| `GET` | `/subscriptions/customers/{customerId}` | List subscriptions by customer |
| `PATCH` | `/subscriptions/{id}/cancel` | Cancel an active subscription |
| `POST` | `/subscriptions/{id}/invoices/generate` | Generate an invoice for a subscription |

### Invoices

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/invoices/{id}` | Get an invoice by ID |
| `GET` | `/invoices/subscriptions/{id}` | List invoices by subscription |
| `PATCH` | `/invoices/{id}/mark-overdue` | Mark an invoice as overdue |

### Payments

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/payments` | Pay an open or overdue invoice |

### Projects

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/projects` | Create a project |
| `GET` | `/projects/{id}` | Get a project by ID |
| `GET` | `/projects/customers/{id}` | List projects by customer |
| `PATCH` | `/projects/{id}/deactivate` | Deactivate a project |
| `PATCH` | `/projects/{id}/activate` | Reactivate a project |

## Running Locally

### Prerequisites

- Java 25 or compatible JDK configured locally.
- Docker and Docker Compose.
- Maven Wrapper included in the project.

### Environment Variables

Create a `.env` file in the project root with the PostgreSQL and pgAdmin settings used by `docker-compose.yml` and `application.yaml`:

```env
POSTGRES_DB=sistema_de_assinaturas
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=admin
```

### Start PostgreSQL and pgAdmin

```bash
docker compose up -d
```

PostgreSQL will be available on:

```text
localhost:5432
```

pgAdmin will be available on:

```text
http://localhost:5050
```

### Run the Spring Boot Application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

Flyway runs automatically on startup and applies the database migrations from:

```text
src/main/resources/db/migration
```
## Tests

The project includes unit tests focused on business rules across the main domains:

- Subscription creation constraints and lifecycle
- Invoice generation and overdue handling
- Payment processing and validation
- Project creation and plan-based limits

Tests are written using JUnit 5 and Mockito, focusing on service layer logic and core business rules rather than simple CRUD operations.
### Run Tests

On Windows:

```powershell
.\mvnw.cmd test
```

On Linux/macOS:

```bash
./mvnw test
```

## Technologies Used

- Java
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker
- Maven
- JUnit 5
- Mockito
- MapStruct
- Lombok

## Project Goal

The goal of this project is to demonstrate backend development skills in a realistic SaaS billing domain. It focuses on implementing business rules in the service layer, modeling entity relationships clearly, validating state transitions, and maintaining a reproducible local development environment with Docker and Flyway.
