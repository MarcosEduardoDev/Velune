# Velune · Hotel Management API

![CI](https://github.com/MarcosEduardoDev/Velune/actions/workflows/ci.yml/badge.svg)
![Java 25](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)

🇧🇷 [Leia em português](README.pt-BR.md)

REST API for hotel management, built with Java and Spring Boot. It manages guests, rooms, reservations and staff schedules, and stores data in PostgreSQL. The project has a layered architecture, business rules covered by unit tests, and a CI pipeline that runs the tests on every push.

> **Status:** portfolio project in active development. See [Known limitations](#known-limitations).

## A 2-minute tour

1. **Reservation rule:** [`ReservaService`](src/main/java/velune/service/ReservaService.java) checks room availability. A stay includes the check-in day and excludes the check-out day, so a room can be booked from the day another guest leaves. The boundary cases are tested in [`ReservaServiceTest`](src/test/java/velune/service/ReservaServiceTest.java).
2. **Error handling:** [`GlobalExceptionHandler`](src/main/java/velune/exception/GlobalExceptionHandler.java) turns business exceptions into `404` and `409`, and validation errors into `400`.
3. **Transaction:** [`FuncionarioService`](src/main/java/velune/service/FuncionarioService.java) creates a staff shift and its notification in a single `@Transactional` operation.
4. **Response DTO:** `GET /funcionarios/{id}/escalas` returns only name, role and shifts. Salary and age are not exposed.
5. **CI:** [`ci.yml`](.github/workflows/ci.yml) runs `mvn test` on every push and pull request.

## API

Field names and error messages are in Portuguese, the domain language of this project.

| Method | Route | Description | Success | Errors |
|---|---|---|---|---|
| `GET` | `/hotel` | Health check | 200 | |
| `GET` | `/hospedes` | List guests | 200 | |
| `GET` | `/hospedes/{documento}` | Find a guest by document | 200 | 404 |
| `POST` | `/hospedes` | Create a guest | 201 | 400, 409 |
| `GET` | `/quartos` | List rooms | 200 | |
| `POST` | `/quartos` | Create a room | 201 | 400, 409 |
| `GET` | `/reservas` | List reservations | 200 | |
| `GET` | `/reservas/{data}` | Reservations that include a date (`yyyy-MM-dd`) | 200 | |
| `POST` | `/reservas` | Create a reservation | 201 | 400, 404, 409 |
| `DELETE` | `/reservas/{quartoId}/{data}` | Cancel the reservation of a room on a date | 204 | 404 |
| `GET` | `/funcionarios` | List employees | 200 | |
| `POST` | `/funcionarios` | Create an employee | 201 | 400 |
| `GET` | `/funcionarios/{id}` | Find an employee | 200 | 404 |
| `POST` | `/funcionarios/{id}/escalas` | Assign a shift and create a notification | 201 | 400, 404 |
| `GET` | `/funcionarios/{id}/escalas` | Employee name, role and shifts | 200 | 404 |

### Example: create a reservation

`POST /reservas`

```json
{
  "hospedeId": 1,
  "quartoId": 1,
  "dataCheckIn": "2026-12-10",
  "dataCheckOut": "2026-12-15",
  "observacao": "Late arrival"
}
```

Returns `201 Created`. If the room is already booked in that period, it returns `409 Conflict` with a message.

### Example: employee shifts

`GET /funcionarios/1/escalas`

```json
{
  "nome": "Ronaldo",
  "cargo": "GERENTE",
  "escalas": [
    { "turno": "MANHA", "data": "2026-12-10" },
    { "turno": "MANHA", "data": "2026-12-11" }
  ]
}
```

## Architecture

```text
HTTP client / web UI
        |
        v
   Controller   receives the request, validates the request DTO
        |
        v
    Service     business rules
        |
        v
   Repository   Spring Data JPA
        |
        v
   PostgreSQL
```

```text
velune
├── controller   REST endpoints
├── service      business rules
├── repository   Spring Data JPA interfaces
├── model        JPA entities and enums
├── dto          request and response objects
└── exception    custom exceptions and the global handler
```

## Design decisions

- **Rules live in the service layer.** Repositories only read and write data.
- **Uniqueness is enforced twice:** a check in the service gives a clear `409`, and `UNIQUE` constraints in the database ([`db/schema.sql`](db/schema.sql)) are the final guarantee. This applies to the guest document and the room number.
- **Business exceptions are unchecked** and handled in one place, so controllers and services stay free of `try/catch`.
- **Request DTOs with Bean Validation** keep invalid data out of the services.
- **The database schema is explicit SQL**, and Hibernate does not alter tables.
- **Services are tested with Mockito**, so the tests need no database and run fast in CI.

## Tests

There are 10 unit tests for the service layer (JUnit 5 and Mockito). They cover:

- duplicate guests and duplicate rooms are rejected;
- room availability, including the check-out day boundary and overlapping dates;
- assigning a shift saves the employee with one shift and one notification;
- the employee shifts card is built correctly, and a missing employee gives a not-found error.

Run them without a database:

```bash
mvn test
```

## Run locally

Requirements: JDK 25, Maven and PostgreSQL.

1. Clone the repository:

```bash
git clone https://github.com/MarcosEduardoDev/Velune.git
cd Velune
```

2. Create a PostgreSQL database named `Velune` and run [`db/schema.sql`](db/schema.sql) in it. The script is safe to run more than once.

3. Set the connection in your terminal (PowerShell example). Never commit real passwords.

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/Velune"
$env:SPRING_DATASOURCE_USERNAME = "your_user"
$env:SPRING_DATASOURCE_PASSWORD = "your_password"
```

4. Start the application:

```bash
mvn spring-boot:run
```

Open `http://localhost:8080/hotel` to check that the API responds.

## Known limitations

- **No authentication or authorization.**
- **Double booking is possible under concurrency.** The availability check runs in Java before saving, so two simultaneous requests could both pass. Database-level protection is planned.
- **Money uses `double`** (salaries and room prices). `BigDecimal` is planned.
- **Most endpoints return entities.** Response DTOs exist only where an entity would expose sensitive fields (employee shifts).
- **Cancellation finds a reservation by room and date**, not by reservation id.
- **No pagination.**
- **Tests cover the service layer only.** There are no controller or integration tests yet.
- **No migration tool and no Docker setup yet.** The schema is a single SQL script.

## Roadmap

- Docker Compose with PostgreSQL
- OpenAPI / Swagger documentation
- Database migrations with Flyway
- Integration tests (controllers and repositories)
- Reservation endpoints by id
- Response DTOs for every endpoint
- Vue.js front end

## About the web UI

The static web UI in `src/main/resources/static` was generated with AI assistance. The focus of this project is the backend. The team page of the UI still uses demo data.

## Author

**Marcos Eduardo Oliveira Silva**, junior Java backend developer.
[LinkedIn](https://www.linkedin.com/in/marcos-eduardo-245587345/) · [GitHub](https://github.com/MarcosEduardoDev)