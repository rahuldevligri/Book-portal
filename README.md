# Book Portal — Spring Boot Web Application

A Java 21 and Spring Boot web application for managing a book catalogue and its related entities, including books, authors, categories, publishers, and distributors. The project combines a server-rendered Thymeleaf UI with Spring Data JPA, Spring Security, database-backed access controls, search APIs, and reporting.

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 4.0.2">
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Thymeleaf-Server--Rendered-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white" alt="Thymeleaf">
</p>

## Highlights

- **Book catalogue:** browse books and view book details, authors, categories, publishers, and related catalogue information.
- **Search and discovery:** search by title, author, or publisher; use advanced search, filtering, pagination, sorting, and match options.
- **Book comparison:** compare up to five books using a feature matrix.
- **Authentication and account management:** registration, login/logout, profile editing, password changes, and a forgot-password flow using security questions.
- **Role- and permission-aware access:** Spring Security configuration with access controls for protected pages and administration features.
- **Administration:** manage users, authors, publishers, distributors, user types, APIs, and access groups/privileges.
- **REST endpoints:** search and administration-related API endpoints.
- **Publisher reporting:** JasperReports integration for publisher reports.
- **Server-rendered UI:** Thymeleaf templates, static assets, and shared header/footer fragments.

## Technology Stack

| Area | Technologies |
|---|---|
| Language and runtime | Java 21 |
| Application framework | Spring Boot 4.0.2, Spring MVC |
| View layer | Thymeleaf, HTML, CSS, JavaScript |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL / MariaDB |
| Security | Spring Security, BCrypt password encoding, JWT libraries for API authentication flows |
| Validation | Spring Boot Validation |
| Reporting | JasperReports 6.21.3 |
| Build | Maven, Maven Wrapper |
| Developer tooling | Lombok |

## Architecture

The application follows a layered Spring architecture:

```text
Browser / API Client
        |
        v
Controllers (MVC / REST)
        |
        v
Services (business rules and workflows)
        |
        v
Repositories (Spring Data JPA)
        |
        v
MySQL / MariaDB
```

Thymeleaf templates render the web pages. The security configuration protects authenticated routes and applies access rules to administrative features and API paths.

## Project Structure

```text
src/main/java/com/example/bookportal/
├── config/          # Application and security configuration
├── controller/      # MVC controllers and API endpoints
├── dto/             # Request and response objects
├── entity/          # JPA entities
├── exception/       # Exception handling
├── repository/      # Spring Data repositories
├── security/        # Security-related components and JWT filter
├── service/         # Business logic
├── specification/   # Dynamic query specifications
├── validation/      # Custom validation
└── BookPortalApplication.java

src/main/resources/
├── db/
│   ├── book_portal.sql       # Database schema and seed data
│   └── sql_files/            # Additional demo/reference SQL files
├── static/                  # CSS, JavaScript, images, and thumbnails
├── templates/               # Thymeleaf pages and reusable fragments
└── application.properties   # Local application configuration
```

## Prerequisites

- JDK 21
- MySQL or MariaDB
- Git

Maven is not required to be installed separately when using the included Maven Wrapper (`mvnw.cmd` on Windows or `./mvnw` on macOS/Linux).

## Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/rahuldevligri/Book-portal.git
cd Book-portal
```

### 2. Create the database

Create a database named `book_portal` in MySQL/MariaDB. Then import the SQL dump included in the project:

```bash
mysql -u root -p book_portal < src/main/resources/db/book_portal.sql
```

Alternatively, select the `book_portal` database in phpMyAdmin and import `src/main/resources/db/book_portal.sql`.

The SQL dump includes table definitions and demo/reference data. Import it into a dedicated local database, not a production database.

### 3. Configure the database connection

Open `src/main/resources/application.properties`. The checked-in configuration currently targets a local database at `localhost:3306`, database `book_portal`, and the common local MySQL `root` account with an empty password. Adjust these properties for your local installation:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/book_portal
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=none
```

You may use your own local username/password. If you use the environment-variable placeholders above, set `DB_USERNAME` and `DB_PASSWORD` in your shell before launching the application. Do not commit real credentials or production secrets.

**Important:** `spring.jpa.hibernate.ddl-auto=none` means Hibernate will not automatically create the schema. Import the SQL file before starting the application.

### 4. Build and test

Windows PowerShell:

```powershell
.\mvnw.cmd clean test
```

macOS/Linux:

```bash
chmod +x mvnw
./mvnw clean test
```

### 5. Start the application

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

## Main Areas to Explore

- `/` — home / login page
- `/register` — account registration
- `/books` — book catalogue
- `/authors`, `/categories`, `/publishers` — catalogue views
- `/advanced-search` — advanced catalogue search
- `/compare` — book comparison
- `/dashboard` — authenticated user dashboard
- `/admin/panel` — administration landing page (requires suitable access)

Some routes require authentication and/or the appropriate access group or privilege.

## Testing

Run the Maven test lifecycle with:

```bash
./mvnw test
```

On Windows, use `mvnw.cmd test`. The repository contains a functionality checklist at [`MUST_KEEP_FUNCTIONALITY_CHECKLIST.md`](MUST_KEEP_FUNCTIONALITY_CHECKLIST.md), which documents expected behavior across authentication, registration, account recovery, catalogue search, comparison, and administration. Review that checklist when making changes.

## Security and Configuration Notes

- Use a dedicated local database account rather than a privileged account where practical.
- Never commit real database passwords, JWT signing keys, mail credentials, or other secrets.
- Password storage uses BCrypt encoding; keep authentication and access-control behavior covered by tests when changing security configuration.
- Keep production error details and database credentials out of user-facing responses and logs.
- This README describes local development. A production deployment needs environment-specific configuration, TLS, least-privilege database access, and a review of authentication/session and API security settings.

## Contributing

1. Fork the repository.
2. Create a focused branch for the change.
3. Run the available tests and review the functionality checklist.
4. Submit a pull request describing the change and its testing.

## License

No explicit license file was found in the repository at the time this README was written. Unless a license is added, reuse and redistribution permissions are not expressly granted.
