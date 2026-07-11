# Surfing Booking Landing - Backend
## Backend API for the Surfing Booking Landing project.
## 🛠️ Tech Stack

| Layer             |
|-------------------|
| Java 21           |
| Spring Boot 4.1.0 |
| Spring Data JPA   |
| PostgreSQL        |
| Liquibase         |
| Docker Compose    |
| MapStruct         | 
| Lombok            |
| Swagger (OpenAPI) |
| JUnit 5           |
| Mockito           |

## How to Run
### 1. Clone the repository
```bash
   git clone https://github.com/rmaksym1/backend.git
```
### 2. Configure Environment Variables
Rename .env.template to .env and update the values if necessary:
```bash
POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB=backend
POSTGRES_LOCAL_PORT=5433

SPRING_LOCAL_PORT=8088
SPRING_DOCKER_PORT=8080

DEBUG_PORT=1200
```
### 3. Run Infrastructure (PostgreSQL)
Ensure you have Docker installed, then run:
```bash
   docker-compose up --build -d
```

### 📊 API Documentation & Links
Once the application is running, you can access:

Interactive Swagger UI: http://localhost:8088/api/swagger-ui/index.html

OpenAPI Specification (JSON): http://localhost:8088/api/v3/api-docs
### 🔄 Database Migrations
Liquibase migrations execute automatically on application startup. No manual schema creation is required