# Basic Calculator API

A production-ready Spring Boot (v3.5.4) REST API for performing basic arithmetic operations. The project is fully
secured using HTTP Basic Authentication backed by an Oracle Database, validates customized API headers via a servlet
interceptor, and logs calculations and errors asynchronously to separate auditing tables.

---

## 🚀 Key Features

- **Arithmetic API:** Supports `add`, `subtract`, `multiply`, and `divide` operations via `/api/calculator/calculate`.
- **Oracle DB Integration:** Connects to an Oracle Pluggable Database (`FREEPDB1`) using a dedicated `CALCULATOR_USER`
  schema.
- **Asynchronous Auditing & Error Logging:** Non-blocking asynchronous logging of calculations to `CALCULATION_HISTORY`
  and failures (e.g., division by zero, invalid payloads) to `CALCULATION_ERROR_LOG`.
- **Spring Security Authentication:** Secures endpoints with HTTP Basic Authentication, validating credentials
  dynamically from the `CALCULATOR_USERS` table with BCrypt password hashing.
- **Header Validation Interceptor:** Requires and validates structured request headers (`x-messageId`,
  `x-appCorrelationId`, `x-consumerType`, `x-client-id`).
- **OpenAPI Codegen:** Contract-first development generating API endpoints and DTOs automatically from `openapi.yaml`.
- **100% Test Coverage:** High-coverage unit and integration tests verified via Maven and JaCoCo reports.

---

## 🗺️ Application Flow Diagram

The text-based flow diagram below outlines the lifecycle of a request, detailing the validation path, the processing
logic, and the asynchronous auditing/logging mechanism:

```text
           +---------------------------------------------+
           |                   Client                    |
           +---------------------------------------------+
                                  |
                                  v
           +---------------------------------------------+
           |            Spring Security Filter           |
           +---------------------------------------------+
            /                                           \
    [Fail] /                                             \ [Pass]
          v                                               v
    +-------------+                       +-----------------------------+
    | 401 response|                       | HeaderValidationInterceptor |
    +-------------+                       +-----------------------------+
                                            /                         \
                                    [Fail] /                           \ [Pass]
                                          v                             v
                                  +--------------+             +------------------+
                                  | Global Error |             | RequestValidator |
                                  |   Handler    |             +------------------+
                                  +--------------+               /              \
                                         |               [Fail] /                \ [Pass]
                                         v                     v                  v
                                  +--------------+      +--------------+   +-------------+
                                  | Async Logger |      | Global Error |   | Calculator  |
                                  |  (logError)  |      |   Handler    |   |  Processor  |
                                  +--------------+      +--------------+   +-------------+
                                         |                     |                  |
                                         v                     v                  v
                                  +--------------+      +--------------+   +-------------+
                                  | Error Log in |      | Async Logger |   | 200 Success |
                                  |  Oracle DB   |      |  (logError)  |   |  Response   |
                                  +--------------+      +--------------+   +-------------+
                                                               |                  |
                                                               v                  v
                                                        +--------------+   +-------------+
                                                        | Error Log in |   | Async Logger|
                                                        |  Oracle DB   |   | (logSuccess)|
                                                        +--------------+   +-------------+
                                                                                  |
                                                                                  v
                                                                           +-------------+
                                                                           | History Log |
                                                                           |  in Oracle  |
                                                                           +-------------+
```

---

## 🛠️ Technology Stack

- **Java Version:** 21
- **Framework:** Spring Boot 3.5.4
- **Database:** Oracle Free Database (23ai/26ai) Pluggable Database (`FREEPDB1`)
- **Persistence:** Spring Data JPA + Hibernate
- **Security:** Spring Security (HTTP Basic Auth)
- **API Spec:** OpenAPI 3.0 / Swagger UI
- **Testing:** JUnit 5, Mockito, Spring Boot Test, JaCoCo (Code Coverage)

---

## 🗄️ Database Setup & Schema

The application connects to Oracle Free Database at `jdbc:oracle:thin:@localhost:1521/FREEPDB1` under the schema
`CALCULATOR_USER`.

### 1. Dedicated Database User Setup (As `SYSDBA`)

For a clean separation of roles, a dedicated `CALCULATOR_USER` should be created with standard `CONNECT`, `RESOURCE`,
and `UNLIMITED TABLESPACE` privileges, as well as permissions to create tables and sequences.

### 2. Application Schema (As `CALCULATOR_USER`)

The application schema setup scripts are located
in [schema.sql](file:///C:/Users/aksha/IdeaProjects/cap-calculator-program-v1/src/main/resources/schema.sql). Running
these scripts creates:

- **Sequences:** PK generators for history, error logs, and user tables (`CALCULATION_HISTORY_SEQ`,
  `CALCULATION_ERROR_LOG_SEQ`, `CALCULATOR_USERS_SEQ`).
- **CALCULATION_HISTORY:** Captures the transaction logging for successful calculations (contains tracking headers,
  operands, operation, result, and timestamps).
- **CALCULATION_ERROR_LOG:** Captures any validation, syntax, or runtime processing errors (contains tracking headers,
  error codes, error messages, and trace details).
- **CALCULATOR_USERS:** User store for dynamic HTTP Basic Authentication containing username, roles, status, and
  BCrypt-hashed passwords (including seeded default admin account).

---

## 🔒 Security & Authentication

Endpoints require HTTP Basic Authentication credentials. The credentials are checked dynamically against the
`CALCULATOR_USERS` table.

- **Public Endpoints:**
    - Health Check: `GET /api/healthCheck`
    - Swagger UI: `GET /swagger-ui/**`
    - OpenAPI Specification: `GET /v3/api-docs/**`
- **Secured Endpoints:**
    - Perform Calculation: `POST /api/calculator/calculate`

### Default Credentials:

- **Username:** `admin`
- **Password:** `password123`

---

## 📋 Required Request Headers

Requests to `/api/calculator/calculate` must include the following headers (validated by `HeaderValidationInterceptor`):

| Header               | Description / Format Constraints                       | Example                                    |
|:---------------------|:-------------------------------------------------------|:-------------------------------------------|
| `x-messageId`        | Must match pattern: `^MSG-[0-9a-fA-F-]{36}$`           | `MSG-550e8400-e29b-41d4-a716-446655440000` |
| `x-appCorrelationId` | Must be a valid UUID string                            | `550e8400-e29b-41d4-a716-446655440000`     |
| `x-consumerType`     | Must be: `MOBILE_APP`, `WEB_APP`, or `INTERNAL_SYSTEM` | `WEB_APP`                                  |
| `x-client-id`        | Alphanumeric with hyphens/underscores, 3 to 50 chars   | `calculator-ui`                            |

---

## 🔌 API Request/Response Samples

### 1. Perform Calculation (Success)

* **Path:** `POST http://localhost:8080/api/calculator/calculate`
* **Authorization:** HTTP Basic (e.g. `admin / password123`)

**Request Payload:**

```json
{
  "operands": {
    "operand1": 10,
    "operand2": 5
  },
  "operation": "add"
}
```

**Response (200 OK):**

```json
{
  "result": {
    "data": {
      "operands": {
        "operand1": 10.0,
        "operand2": 5.0
      },
      "operation": "ADD",
      "value": 15.0
    }
  }
}
```

### 2. Error Response (400 Bad Request - Division by Zero)

**Request Payload:**

```json
{
  "operands": {
    "operand1": 10,
    "operand2": 0
  },
  "operation": "divide"
}
```

**Response (400 Bad Request):**

```json
{
  "result": {
    "errors": [
      {
        "code": "1002",
        "message": "Invalid Body Parameter",
        "details": "Cannot divide by zero. operand2 must not be 0"
      }
    ]
  }
}
```

---

## 🛠️ Run & Test Instructions

### Build and Run Locally

```bash
# Clean, compile and package
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run
```

### Run Tests and Generate Reports (Surefire & JaCoCo)

The application uses **Maven Surefire** to run the test suite and **JaCoCo** to measure instruction/branch coverage.

```bash
# 1. Run all tests and generate the JaCoCo coverage binary data
mvn clean verify test

# 2. Generate a readable HTML test execution report using Maven Surefire Report Plugin
mvn surefire-report:report
```

#### Viewing the Reports:

- **JaCoCo Coverage Report:** Open `target/site/jacoco/index.html` in any browser to review line and branch coverage
  metrics.
- **Surefire Test Execution Report:** Open `target/site/surefire-report.html` in any browser to view test durations,
  detailed assertions, status, and failure traces.
