# Book Portal Project Workflow Documentation

## Overview

Book Portal is a Spring Boot web application for managing books, authors, publishers, and categories. It uses Spring MVC, Spring Data JPA, Spring Security, Thymeleaf, and MySQL.

---

## Prerequisites
- Java 21 or higher
- Maven
- MySQL database

---

## Project Structure
- `src/main/java/com/example/bookportal/` - Main Java source code
  - `controller/` - Web controllers (handle HTTP requests)
  - `service/` - Business logic
  - `repository/` - Data access (Spring Data JPA)
  - `entity/` - JPA entities (database models)
  - `dto/` - Data Transfer Objects
  - `config/` - Security and application configuration
  - `exception/` - Custom exception handling
  - `specification/` - JPA specifications for advanced queries
  - `validation/` - Custom validators
- `src/main/resources/`
  - `application.yml` - Main configuration file
  - `templates/` - Thymeleaf HTML templates
  - `static/` - Static resources (CSS, images)
- `src/test/java/` - Unit and integration tests

---

## Build & Run

1. **Configure Database**
   - Edit `src/main/resources/application.yml` with your MySQL credentials and database name.

2. **Build the Project**
   - Run: `mvn clean install`

3. **Run the Application**
   - Run: `mvn spring-boot:run`
   - Or execute the generated JAR: `java -jar target/book-portal-0.0.1-SNAPSHOT.jar`

4. **Access the App**
   - Open [http://localhost:8080](http://localhost:8080) in your browser.

---

## Development Workflow

- **Controllers** handle web requests and return views or REST responses.
- **Services** contain business logic and interact with repositories.
- **Repositories** provide CRUD operations for entities.
- **Entities** map to database tables.
- **Templates** (Thymeleaf) render dynamic HTML pages.
- **Static** resources are served as-is.
- **Security** is configured in the `config` package.

---

## Testing
- Place tests in `src/test/java/com/example/bookportal/`
- Run tests with: `mvn test`

---

## Useful Commands
- Build: `mvn clean install`
- Run: `mvn spring-boot:run`
- Test: `mvn test`

---

## References
- See `HELP.md` for official guides and documentation links.

---

## Contribution
- Fork the repository, create a feature branch, commit changes, and open a pull request.

---

## License
- See `pom.xml` for license details.
