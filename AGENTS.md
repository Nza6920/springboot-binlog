# Repository Guidelines

## Project Structure & Module Organization
This repository is a Maven-based Spring Boot project for consuming MySQL binlog events. Java sources live under `src/main/java/com/niu/springboot`, split between `autoconfig` for starter-style configuration and `binlog` for application logic, DTOs, enums, and services. Runtime resources are under `src/main/resources`, including `application.yml.example`, `logback-spring.xml`, `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`, and `test.sql`. There is currently no `src/test/java` tree; add new tests there when introducing test coverage.

## Build, Test, and Development Commands
- `./mvnw clean package` — compile the project and build the runnable JAR.
- `./mvnw test` — run the Spring Boot/JUnit test suite.
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` — start the app locally when you have a real `application.yml`.
- `./mvnw dependency:tree` — inspect dependency conflicts when changing connector or JDBC libraries.

Use Java 21. The repository includes Maven Wrapper, so a separate Maven install is optional.

## Coding Style & Naming Conventions
Follow standard Java conventions: 4-space indentation, `UpperCamelCase` for classes, `lowerCamelCase` for fields and methods, and `UPPER_SNAKE_CASE` for enum constants. Keep packages under `com.niu.springboot.*` and place implementations in `.service.impl`. Prefer constructor injection; Lombok annotations such as `@AllArgsConstructor` and `@Slf4j` are already used in this codebase. Keep configuration keys and sample values aligned with `src/main/resources/application.yml.example`.

## Testing Guidelines
Use `spring-boot-starter-test` for unit and integration tests. Place tests in `src/test/java` mirroring the main package path, and name files `*Test` (unit) or `*IT` (integration). Focus coverage on binlog position persistence, dictionary lookups, and auto-configuration behavior. For SQL-backed scenarios, document any required seed data and reuse patterns from `src/main/resources/test.sql` where practical.

## Commit & Pull Request Guidelines
Recent history shows short, imperative commit messages, often in Chinese, such as `新增连接池` or `修改启动类逻辑`. Keep commits focused and descriptive; one logical change per commit is preferred. Pull requests should include: a short summary, affected modules, local verification steps (for example `mvn test`), and any config or schema changes. Add sample request/response logs or screenshots only when behavior changes are user-visible.

## Configuration Notes
Do not commit real database credentials or binlog endpoints. Copy `src/main/resources/application.yml.example` to a local, ignored `application.yml` for development, and update examples when introducing new required properties.

## Agent Notes
When reading files that may contain Chinese text, use UTF-8 decoding first to avoid garbled output in the terminal.
