# 🧮 Basic Calculator API

A production-ready Spring Boot (v3.5.4) REST API for performing arithmetic operations, managing user accounts, and providing structured audit trails. Backed by Oracle Database, secured with Spring Security, and integrated with Seq for Splunk-like log aggregation.

---

## 🧭 Navigation Tabs

| [🗺️ Flow Diagrams](#-application-flow-diagrams) | [🗄️ DB & Schema](#-database-setup--schema) | [🔌 API & Samples](#-api-endpoints--request-samples) | [🛠️ Run & Test](#-run--test-instructions) | [🧪 Postman Tests](#-postman-integration) | [📊 Seq & Scheduler](#-local-diagnostic-logging--maintenance-seq) |
| :--- | :--- | :--- | :--- | :--- | :--- |

---

## 🚀 Key Features

| Core API | Enterprise Logging | Log Maintenance | Security & RBAC | Header Validation | Test Coverage |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Core `/calculator/calculate` supporting `ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`. | Local **Seq** using Compact Log Event Format (CLEF) & Splunk lower_snake_case properties. | Automatic hourly DB pruner sweep & immediate JVM startup cleanup. | Dynamic Basic Auth with BCrypt hashing from `CALCULATOR_USERS`. | Interceptor validating message UUID format & client credentials. | **92%+** instruction and branch coverage via Surefire/JaCoCo. |

---

## 🗺️ Application Flow Diagrams

<details>
<summary><b>🔍 Click to view Request Lifecycle & Class Flows</b></summary>

### 1. POST /calculator/calculate — Arithmetic Calculation
```text
Client ──► Spring Security (Basic Auth) ──► Header Interceptor (UUID Check) ──► Validator ──► Processor ──► Audit Log ──► 200 OK
```

### 2. POST /users/register — User Registration
```text
Client ──► Permit All Path ──► Controller ──► Service (Duplicate Username Check) ──► BCrypt Encode ──► Save User ──► 200 OK
```

### 3. GET /calculator/history & GET /calculator/errors
```text
Client ──► Spring Security ──► Role Check (ADMIN/USER) ──► HistoryService ──► Fetch Table ──► 200 OK
```
</details>

---

## 🗄️ Database Setup & Schema

<details>
<summary><b>💾 Click to view Schema Details & SQL Scripts</b></summary>

| Database URL | User Setup (SYSDBA) | Schema Tables |
| :--- | :--- | :--- |
| `jdbc:oracle:thin:@localhost:1521/FREEPDB1` | `CREATE USER CALCULATOR_USER IDENTIFIED BY Password123;`<br>`GRANT CONNECT, RESOURCE, UNLIMITED TABLESPACE TO CALCULATOR_USER;` | **`CALCULATION_HISTORY`**: Logs calculations<br>**`CALCULATION_ERROR_LOG`**: Logs runtime exceptions<br>**`CALCULATOR_USERS`**: Secure user database |
</details>

---

## 🔌 API Endpoints & Request Samples

<details>
<summary><b>📡 Click to view HTTP Endpoints, Headers, & Payloads</b></summary>

### Endpoint Access & Headers

| Method | Endpoint | Authorization | Required Calculation Headers (POST Only) |
| :--- | :--- | :--- | :--- |
| `GET` | `/healthCheck` | Public | None |
| `POST` | `/users/register` | Public | None |
| `POST` | `/calculator/calculate` | Any authenticated user | `x-messageId` (Pattern `^MSG-[0-9a-fA-F-]{36}$`), `x-appCorrelationId` (UUID), `x-consumerType`, `x-client-id` |
| `GET` | `/calculator/history` | Any authenticated user | None |
| `GET` | `/calculator/errors` | `ADMIN` only | None |

### Request & Error Response Samples

| Sample Request JSON | Sample Error Response (400) | Error Code Definitions |
| :--- | :--- | :--- |
| <pre>{<br>  "operands": {<br>    "operand1": 10,<br>    "operand2": 5<br>  },<br>  "operation": "add"<br>}</pre> | <pre>{<br>  "result": {<br>    "errors": [<br>      {<br>        "code": "1002",<br>        "message": "Invalid Body Parameter",<br>        "details": "Cannot divide by zero"<br>      }<br>    ]<br>  }<br>}</pre> | **`1001`**: Header validation failed<br>**`1002`**: Body parameter invalid / Math logic error<br>**`1003`**: Invalid operation type |
</details>

---

## 🛠️ Run & Test Instructions

<details>
<summary><b>💻 Click to view build, test, and reporting commands</b></summary>

| Build & Run Command | Test & Coverage Command | Verification Reports |
| :--- | :--- | :--- |
| `mvn clean install` <br> `mvn spring-boot:run` | `mvn clean test` <br> `mvn surefire-report:report` | **JaCoCo:** `target/site/jacoco/index.html` <br> **Surefire:** `target/site/surefire-report.html` |
</details>

---

## 🧪 Postman Integration

<details>
<summary><b>🎯 Click to view Postman/Newman Collection details</b></summary>

### Newman Test Suite Coverages
| Suite Size | Verified Scenarios | Latest Result |
| :--- | :--- | :--- |
| **30 Requests** <br> **58 Assertions** | GET `/healthCheck` status verification <br> Arithmetic operations & division by zero error handling <br> Missing/Invalid header format validations <br> Dynamic headers checking via `{{$guid}}` <br> User registration duplicate testing <br> RBAC authorization verification (401/403 blocks) | ✅ **58 / 58 assertions passed (100%)** |
</details>

---

## 📊 Local Diagnostic Logging & Maintenance (Seq)

<details>
<summary><b>📝 Click to view Seq Queries & Automatic Database Pruner configurations</b></summary>

### Structured Logging & Ingestion
- **CLEF Event Format:** Custom `SeqAppender` serializes SLF4J logs into Compact Log Event Format (CLEF) pushing to `http://localhost:5341`.
- **Splunk Alignment:** MDC fields map to `app_correlation_id`, `message_id`, `consumer_id`, `originating_system_id`, `log_category`, `appid`.
- **Inline Body Logging:** Request & Response JSON bodies are printed openly in the main feed message:
  `Method Execution Time taken :: @@@ POST /calculator/calculate @@@ ELAPSEDMS=72 ms @@@ ReqBody: [JSON] | RespBody: [JSON]`
- **Exceptions:** Full multi-line Java stack trace is logged under the native `@x` property on bad requests/exceptions.

### Maintenance & Queries

| Automatic 24-Hour Pruner | Useful Seq Filter Queries | Manual Database Purging |
| :--- | :--- | :--- |
| **Hourly Sweeper:** `CleanupService` cron (`0 0 * * * *`) deletes history/error log DB rows older than 24h. <br> **JVM Startup Sweep:** Runs an initial pruner sweep immediately on app startup. | **API logs:** `log_category == "API"` <br> **Newman traffic:** `Header_user-agent cp "Newman"` <br> **Errors & warnings:** `status >= 400` | Run `DELETE FROM CALCULATION_HISTORY WHERE CREATED_AT < SYSDATE - 1;` and commit.<br> Alternatively, execute `./prune-logs-and-db.ps1`. |
</details>
