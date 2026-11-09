# EventHub Spring Boot Backend

A REST API for event organisers to create events, admins to approve them, and customers to browse events and book tickets.

## Features

- User registration and login
- JWT-based authentication and role-based access
- Event creation by organisers
- Event approval and status updates by admins
- Approved event listing and ticket booking by customers
- Booking confirmation email support

## Technology

- Java
- Spring Boot
- Spring Security and JWT
- Spring Data JPA and Hibernate
- MySQL
- Maven

## Run locally

1. Install Java and MySQL.
2. Create a MySQL database named `event-hub-spring`.
3. Configure your database and email settings in `src/main/resources/application.properties`.
4. Keep passwords, API keys, and other secrets out of GitHub. Use environment variables for credentials.
5. From the project folder, run on Windows:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The API runs at `http://localhost:9181` by default.

## Authentication

Register and log in using the `/auth` endpoints. Login returns a JWT token. For protected requests, set this Postman header:

```text
Authorization: Bearer <your-token>
```

Public registration assigns the `ROLE_CUSTOMER` role. Organiser and admin roles should be assigned through a trusted setup process.

## API endpoints

| Method | Endpoint | Role | Purpose |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register a customer |
| `POST` | `/auth/login` | Public | Log in and receive a JWT |
| `POST` | `/organiser/registerEvent` | Organiser | Create an event |
| `GET` | `/organiser/test` | Organiser | Check JWT authentication |
| `GET` | `/admin/findEventByStatusPending` | Admin | List pending events |
| `PUT` | `/admin/approveEvents` | Admin | Approve all pending events |
| `POST` | `/admin/changeEventStatus` | Admin | Change one event’s status |
| `GET` | `/customer/getAllApprovedEvents` | Customer | List approved events |
| `GET` | `/customer/bookEvent` | Customer | Book tickets |

### Register an event

Send JSON to `POST /organiser/registerEvent`:

```json
{
  "name": "Community Music Night",
  "description": "An evening of live music",
  "location": "City Hall",
  "eventDateTime": "2026-12-15T18:30:00",
  "ticketPrice": 25.00,
  "availableTickets": 100
}
```

The event starts with `PENDING` status.

### Change event status

Send `POST /admin/changeEventStatus` with query parameters:

```text
eventId=1
status=APPROVED
```

Allowed statuses are `APPROVED`, `PENDING`, and `REJECTED`.

### Book tickets

Send `GET /customer/bookEvent` with query parameters:

```text
eventId=1
ticketQuantity=1
```

The event must be approved and have enough available tickets.

## Email and payments

Booking confirmation email sending requires valid email-provider configuration. Payment processing is not connected yet; the current booking code marks bookings as `PAID` as a placeholder.
