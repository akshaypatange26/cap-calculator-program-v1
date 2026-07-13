# Basic Calculator API

A production-ready Spring Boot (v3.5.4) REST API for performing basic arithmetic operations, managing user accounts,
and providing full audit trails. The project is fully secured using HTTP Basic Authentication backed by an Oracle
Database, validates customized API headers via a servlet interceptor, and logs calculations and errors asynchronously
to separate auditing tables.

---

## 🚀 Key Features

- **Arithmetic API:** Supports `add`, `subtract`, `multiply`, and `divide` operations via `/calculator/calculate`.
- **Calculation History:** Retrieve a full log of all successful calculations via `GET /calculator/history` (requires authentication).
- **Audit & Error Logs:** Retrieve all error and validation failure logs via `GET /calculator/errors` (requires ADMIN role).
- **User Registration:** Publicly register new users via `POST /users/register` (no authentication needed).
- **Oracle DB Integration:** Connects to an Oracle Pluggable Database (`FREEPDB1`) using a dedicated `CALCULATOR_USER` schema.
- **Asynchronous Auditing & Error Logging:** Non-blocking asynchronous logging of calculations to `CALCULATION_HISTORY` and failures (e.g., division by zero, invalid payloads) to `CALCULATION_ERROR_LOG`.
- **Spring Security Authentication:** Secures endpoints with HTTP Basic Authentication, validating credentials dynamically from the `CALCULATOR_USERS` table with BCrypt password hashing.
- **Role-Based Access Control:** `ADMIN` users access all endpoints; `USER` role is restricted from error logs.
- **Header Validation Interceptor:** Requires and validates structured request headers (`x-messageId`, `x-appCorrelationId`, `x-consumerType`, `x-client-id`) on calculation endpoints only.
- **OpenAPI Codegen:** Contract-first development generating API endpoints and DTOs automatically from `openapi.yaml`.
- **92% Code Coverage:** High-coverage unit and integration tests verified via Maven and JaCoCo reports.

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
    +-------------+                       | (skipped for GET endpoints) |
                                          +-----------------------------+
                                            /                         \
                                    [Fail] /                           \ [Pass]
                                          v                             v
                                  +--------------+             +------------------+
                                  | Global Error |             | RequestValidator  |
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
- **Security:** Spring Security (HTTP Basic Auth + RBAC)
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
in [schema.sql](src/main/resources/schema.sql). Running
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

| Endpoint | Access Level |
|:---|:---|
| `GET /healthCheck` | Public (no auth needed) |
| `GET /swagger-ui/**` | Public |
| `GET /v3/api-docs/**` | Public |
| `POST /users/register` | Public (no auth needed) |
| `POST /calculator/calculate` | Any authenticated user (`USER` or `ADMIN`) |
| `GET /calculator/history` | Any authenticated user (`USER` or `ADMIN`) |
| `GET /calculator/errors` | `ADMIN` role only |

### Default Admin Credentials:

- **Username:** `admin`
- **Password:** `password123`
- **Role:** `ADMIN`

> New users registered via `POST /users/register` are automatically assigned the `USER` role.

---

## 📋 Required Request Headers

Requests to `POST /calculator/calculate` must include the following headers (validated by `HeaderValidationInterceptor`).
`GET` endpoints (`/calculator/history`, `/calculator/errors`) do **not** require these headers.

| Header               | Description / Format Constraints                       | Example                                    |
|:---------------------|:-------------------------------------------------------|:-------------------------------------------|
| `x-messageId`        | Must match pattern: `^MSG-[0-9a-fA-F-]{36}$`           | `MSG-550e8400-e29b-41d4-a716-446655440000` |
| `x-appCorrelationId` | Must be a valid UUID string                            | `550e8400-e29b-41d4-a716-446655440000`     |
| `x-consumerType`     | Must be: `MOBILE_APP`, `WEB_APP`, or `INTERNAL_SYSTEM` | `WEB_APP`                                  |
| `x-client-id`        | Alphanumeric with hyphens/underscores, 3 to 50 chars   | `calculator-ui`                            |

---

## 🔌 API Endpoints & Samples

### 1. Perform Calculation (Success)

- **Method:** `POST`
- **Path:** `http://localhost:8080/calculator/calculate`
- **Authorization:** HTTP Basic (`admin / password123`)
- **Required Headers:** See table above

**Request:**
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

---

### 2. Register a New User

- **Method:** `POST`
- **Path:** `http://localhost:8080/users/register`
- **Authorization:** None (public endpoint)

**Request:**
```json
{
  "username": "myUser",
  "password": "SecurePass123"
}
```

**Response (200 OK):**
```json
{
  "id": 5,
  "username": "myUser",
  "message": "User registered successfully"
}
```

---

### 3. Get Calculation History

- **Method:** `GET`
- **Path:** `http://localhost:8080/calculator/history`
- **Authorization:** HTTP Basic (any authenticated user)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "messageId": "MSG-550e8400-e29b-41d4-a716-446655440000",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000",
    "consumerType": "WEB_APP",
    "clientId": "calculator-ui",
    "operand1": 10.0,
    "operand2": 5.0,
    "operation": "add",
    "resultValue": 15.0,
    "createdAt": "2026-07-13T10:00:00Z"
  }
]
```

---

### 4. Get Error Logs (ADMIN Only)

- **Method:** `GET`
- **Path:** `http://localhost:8080/calculator/errors`
- **Authorization:** HTTP Basic (`admin / password123` — ADMIN role required)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "messageId": "MSG-550e8400-e29b-41d4-a716-446655440000",
    "correlationId": "550e8400-e29b-41d4-a716-446655440000",
    "consumerType": "WEB_APP",
    "clientId": "calculator-ui",
    "errorCode": "1002",
    "errorMessage": "Invalid Body Parameter",
    "errorDetails": "Cannot divide by zero. operand2 must not be 0",
    "createdAt": "2026-07-13T10:00:00Z"
  }
]
```

---

### 5. Error Response (400 Bad Request — Division by Zero)

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

### Error Codes

| Code   | Meaning                          |
|:-------|:---------------------------------|
| `1001` | Missing or invalid request header |
| `1002` | Invalid or missing body parameter |
| `1003` | Invalid arithmetic operation enum |

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
mvn clean test

# 2. Generate a readable HTML test execution report using Maven Surefire Report Plugin
mvn surefire-report:report
```

#### Viewing the Reports:

- **JaCoCo Coverage Report:** Open `target/site/jacoco/index.html` in any browser to review line and branch coverage metrics.
- **Surefire Test Execution Report:** Open `target/site/surefire-report.html` in any browser to view test durations, detailed assertions, status, and failure traces.

---

## 🧪 Postman Test Suite

A Postman collection (`Basic_Calculator_Test_Suite.postman_collection.json`) is included in the root of the project covering **21 requests / 56 assertions**:

| Category | Tests Covered |
|:---|:---|
| Arithmetic operations | Add, Subtract, Multiply, Divide, Divide by Zero |
| Missing headers (individual) | `x-messageId`, `x-appCorrelationId`, `x-consumerType`, `x-client-id` |
| Invalid headers (individual) | Format, UUID, enum, length violations |
| Combined header failures | 1 missing + 1 wrong, 2 missing simultaneously |
| Body validation | Empty body, `{}`, missing operands, missing operation, wrong enum |
| User Registration | Success, duplicate username |
| History | Admin access, USER access |
| Error Logs | Admin success, USER forbidden (403), unauthenticated (401) |

**Latest run result: ✅ 56/56 assertions passed (100%)**
