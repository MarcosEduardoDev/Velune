# Velune · Hotel Management API

![CI](https://github.com/MarcosEduardoDev/Velune/actions/workflows/ci.yml/badge.svg)
![Java 25](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)

🇧🇷 [Leia em português](README.pt-BR.md)

A REST API for hotel management, built with Java 25 and Spring Boot. Velune manages guests, rooms, reservations, and employee shifts, using PostgreSQL for data persistence.

The project is under active development and includes a layered API, business rules tested with JUnit and Mockito, a static web interface, and a PostgreSQL development environment managed with Docker Compose.

## Features

- Create and look up guests, with duplicate document checks.
- Create and list rooms, with unique room numbers and predefined room types.
- Create, search by date, and cancel reservations.
- Check room availability for the requested stay period.
- Create and look up employees.
- Assign employee shifts, creating an associated notification.
- A web interface for the dashboard, guests, rooms, reservations, and team pages.

## Technologies

- Java 25
- Spring Boot 4.1.1
- Spring Web and Bean Validation
- Spring Data JPA
- PostgreSQL 16
- Maven
- JUnit 5 and Mockito
- Docker Compose
- GitHub Actions

## API

Route names, request fields, and error messages follow the project’s domain language and are in Portuguese.

| Method | Route | Description | Success | Errors |
|---|---|---|---|---|
| `GET` | `/hotel` | Checks whether the API is responding | 200 | |
| `GET` | `/hospedes` | Lists guests | 200 | |
| `GET` | `/hospedes/{documento}` | Finds a guest by document | 200 | 404 |
| `POST` | `/hospedes` | Creates a guest | 201 | 400, 409 |
| `GET` | `/quartos` | Lists rooms | 200 | |
| `POST` | `/quartos` | Creates a room | 201 | 400, 409 |
| `GET` | `/reservas` | Lists reservations | 200 | |
| `GET` | `/reservas/{data}` | Lists reservations active on a date (`yyyy-MM-dd`) | 200 | |
| `POST` | `/reservas` | Creates a reservation | 201 | 400, 404, 409 |
| `DELETE` | `/reservas/{quartoId}/{data}` | Cancels the room’s active reservation on a date | 204 | 404 |
| `GET` | `/funcionarios` | Lists employees | 200 | |
| `POST` | `/funcionarios` | Creates an employee | 201 | 400 |
| `GET` | `/funcionarios/{id}` | Finds an employee by ID | 200 | 404 |
| `POST` | `/funcionarios/{id}/escalas` | Assigns a shift and creates a notification | 201 | 400, 404 |
| `GET` | `/funcionarios/{id}/escalas` | Returns the employee’s name, role, and shifts | 200 | 404 |

Validation errors return `400` with a JSON object mapping fields to messages. Business exceptions return `404` or `409`, depending on the error.

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

A reservation includes the check-in date and excludes the check-out date. This means another stay can begin on the same day the previous one ends. An invalid period or missing required field returns `400`; a missing guest or room returns `404`; an availability conflict returns `409`.

## Architecture

The API follows a layered architecture:

```text
Web interface / HTTP client
             |
             v
        Controller   REST routes and DTO validation
             |
             v
         Service      business rules
             |
             v
       Repository     data access with Spring Data JPA
             |
             v
         PostgreSQL
```

The main packages are organized by responsibility:

```text
velune
├── controller   REST endpoints
├── service      business rules
├── repository   Spring Data JPA data access
├── model        JPA entities and enums
├── dto          request and response objects
└── exception    business exceptions and global error handling
```

The SQL schema is in [`db/schema.sql`](db/schema.sql). Docker Compose mounts this file for PostgreSQL initialization.

## Run with Docker

Requirements: Docker Desktop with Docker Compose, JDK 25, and Maven. Compose starts the PostgreSQL database; the API runs locally through Maven.

1. Clone the repository:

```powershell
git clone https://github.com/MarcosEduardoDev/Velune.git
cd Velune
```

2. Create the `.env` file and replace `POSTGRES_PASSWORD` with your own password:

```powershell
copy .env.example .env
```

On Linux or macOS, use `cp .env.example .env`.

3. Start the database:

```powershell
docker compose up -d
```

When the database is initialized for the first time, PostgreSQL runs `db/schema.sql` to create the tables.

4. Configure the API connection in PowerShell, using the same password set in `.env`:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5433/velune'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = 'the-same-password-set-in-env'
mvn spring-boot:run
```

On Linux or macOS, set the same variables with `export` and run `mvn spring-boot:run`:

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5433/velune'
export SPRING_DATASOURCE_USERNAME='postgres'
export SPRING_DATASOURCE_PASSWORD='the-same-password-set-in-env'
mvn spring-boot:run
```

5. Open <http://localhost:8080/hotel> to check the API response. The web interface is available at <http://localhost:8080/>. If no rooms have been created yet, `GET /quartos` returns `[]`.

To stop and remove the container:

```powershell
docker compose down
```

Compose does not define a named data volume. The PostgreSQL image may create an anonymous volume: `docker compose down` does not remove it by default, and a later `up` does not automatically reuse it. To also remove these volumes and start with an empty database, run `docker compose down -v`. [Docker Compose reference](https://docs.docker.com/reference/cli/docker/compose/down/).

## Run with a local PostgreSQL database

You can also run the API without Docker. Install JDK 25, Maven, and PostgreSQL, create a database named `velune`, and run [`db/schema.sql`](db/schema.sql) against it.

Configure the connection in PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/velune'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = 'your_password'
mvn spring-boot:run
```

Do not commit real passwords. The `.env` file is listed in `.gitignore`.

## Tests

The current tests are unit tests for the service layer, written with JUnit 5 and Mockito. They do not require a database:

```bash
mvn test
```

The suite covers guest and room creation, reservation availability rules, overlapping stays, the check-out/check-in boundary, shift assignment, and creating the employee shifts response DTO.

The [`ci.yml`](.github/workflows/ci.yml) workflow runs `mvn test` on pushes to `master` and on pull requests.

## Web interface

The static interface is in `src/main/resources/static` and is served by the Spring Boot application. It supports browsing and creating guests and rooms, as well as creating, viewing, and cancelling reservations.

Some parts still use sample data, including the dashboard’s daily movements and the team page. The interface does not yet represent every API operation with live data.

## Known limitations

- The API does not yet have authentication or authorization.
- Availability is checked in the Java service layer; two simultaneous requests could both pass the check before either reservation is saved.
- Salaries and room rates use `double`.
- Most endpoints return entities directly. The employee shifts endpoint uses a response DTO, but other employee endpoints still return the entity.
- Reservation cancellation finds a reservation by room and date, not by reservation ID.
- There is no pagination yet.
- Tests cover the service layer only; there are no controller or integration tests yet.
- There is no database migration tool. The schema is maintained in a single SQL script.

## Author

**Marcos Eduardo Oliveira Silva** · Junior Java Backend Developer

[LinkedIn](https://www.linkedin.com/in/marcos-eduardo-245587345/) · [GitHub](https://github.com/MarcosEduardoDev)