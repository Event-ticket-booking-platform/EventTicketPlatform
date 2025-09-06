

# Event Ticket Booking – Backend
Microservices backend for the Event Ticket Booking Platform, running behind an API Gateway. Includes services for Users, Events, Tickets, Orders, Payments and Notifications, plus Kafka for async events and Swagger UI aggregation.

#### Frontend (live): https://front-end-theta-wine.vercel.app/
#### Gateway (local default): http://localhost:19090
#### Swagger (aggregated at gateway): http://localhost:19090/swagger/index.html

## ✨ What’s Inside

- **API Gateway (YARP)**
  - CORS configured for allowed front-ends
  - Aggregated Swagger UI for all services

- **Microservices**
  - Users
  - Events
  - Tickets
  - Orders
  - Payments
  - Notifications

- **Datastores**
  - PostgreSQL (multiple instances)
  - MongoDB (for notifications & Event)

- **Messaging**
  - Kafka & Zookeeper (domain events)
  - Kafka UI (port defined in `docker-compose.yml`)

- **Health Checks**
  - Gateway root: `/`
  - Liveness: `/healthz`


## 🧱 Tech Stack
- **Backend:** .NET (ASP.NET Core) microservices, Spring Boot
- **API Gateway:** YARP Reverse Proxy
- **Databases:** PostgreSQL, MongoDB
- **Message Broker:** Kafka + Zookeeper
- **Containerization:** Docker & Docker Compose

## ✅ Prerequisites
- Docker Desktop (Windows/macOS) or Docker Engine (Linux)
- Docker Compose v2
- (Optional) .NET SDK / Java JDK — if you want to run/build services outside Docker

## 🚀 Quick Start (Docker)
These commands start all services and databases required by the project.

1) **Clone & enter the project**
 
   ```bash
    # replace with your backend repo URL if needed
      git clone https://github.com/Event-ticket-booking-platform/EventTicketPlatform.git
      cd EventTicketPlatform
    ```

2) **Build & run (detached)**

 ```bash
    docker compose up -d --build
   ```

3) **Check everything is healthy**
 ```bash
    docker compose ps
   ```
3) **Open the Services**
Gateway root: http://localhost:19090

Swagger (aggregated): http://localhost:19090/swagger/index.html

Kafka UI: http://localhost:8088

Pair this with the FrontEnd by setting VITE_API_BASE_URL=http://localhost:19090 and running the frontend on http://localhost:8080.



Use those base paths via the gateway (not direct container ports) for local development and when pointing the frontend.

## 🧩 Troubleshooting
Ports Already in Use
Stop other apps using the same ports (e.g., 19090, 8088 for Kafka UI) or adjust mappings in docker-compose.yml.

401 Unauthorized
Your Authorization: Bearer <token> is missing or expired. Log in via UserService to get a fresh JWT.
