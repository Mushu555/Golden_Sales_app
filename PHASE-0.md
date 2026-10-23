# Golden Sales App — Phase 0

## Scope

This phase stabilizes the existing Product and Customer Spring Boot services without merging them. It adds request/response records, Bean Validation, RFC 9457-style `ProblemDetail` errors, pagination/search, soft deactivation, Flyway migrations, Actuator health probes, and safer production defaults.

## Service ownership

- Product service: `backend/`; set `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` to the product database (`salesdb` was the proposed mapping, not verified against your Neon settings).
- Customer service: `customer-service/`; set `CUSTOMER_DATABASE_URL`, `CUSTOMER_DATABASE_USERNAME`, `CUSTOMER_DATABASE_PASSWORD` to the customer database (`customerdb` was the proposed mapping, not verified against your Neon settings).
- Leave `neondb` untouched until you confirm its purpose.

For Neon, use the JDBC URL format expected by the PostgreSQL driver, for example `jdbc:postgresql://HOST/DB?sslmode=require`. Keep credentials in environment variables and never commit them.

## API changes

- List endpoints now return Spring `Page` JSON and support `page`, `size`, `sort`.
- `GET /api/products?search=milk` searches active product names.
- `GET /api/customers?search=ahmed` searches active customer names.
- `GET /api/customers/name/{name}` is removed because names are not unique. Use the search endpoint instead.
- DELETE endpoints now deactivate records instead of physically deleting them.
- Controllers return DTOs rather than persistence entities.
- Validation errors return HTTP 400; missing/inactive resources return HTTP 404; uniqueness conflicts return HTTP 409.

## Build and test

Run from the repository root:

```bash
cd backend && ./gradlew clean test
cd ../customer-service && ./gradlew clean test
```

The test task needs Java 21 and Maven Central access. These commands do not connect to Neon unless you configure test/application properties to do so.

## Migration safety

Back up the target Neon databases before deploying. Flyway migration V1 uses `CREATE TABLE IF NOT EXISTS` and adds the `active` column if missing, to accommodate the existing schema. It does not drop tables or data. First run against a disposable Neon branch or local PostgreSQL, verify Flyway's history and Hibernate validation, then roll out to production. Do not point both services at the same database.

## Known Phase 0 limitations

Authentication/authorization, gateway routing, SKU/category/reorder-level fields, phone-number country-aware canonicalization, unique normalized phone migration, integration tests with Testcontainers, OpenAPI, and Android implementation remain later work. The current phone normalization removes whitespace and common punctuation; it does not infer country codes.
