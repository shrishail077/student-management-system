# Student Management System

A REST API built with Spring Boot 3 for managing student admissions, course assignments, and enrollments. Built as a
backend assignment for Platform Commons Foundation.

---

## Tech Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Security** with stateless JWT authentication
- **Spring Data JPA** / Hibernate (MySQL in production, H2 for tests)
- **Flyway** for database migrations
- **MapStruct** for DTO mapping, **Lombok** for boilerplate reduction
- **Swagger/OpenAPI 3** for API documentation

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

---

## Default Admin Credentials

Seeded automatically by Flyway on first startup:

| Username | Password    |
|----------|-------------|
| `admin`  | `Admin@123` |

---

## Running the Application

### 1. Create the database

```sql
CREATE DATABASE sms_db;
```

### 2. Configure credentials

The defaults in `src/main/resources/application.yml` assume `root / root`. If yours differ, update:

```yaml
spring:
  datasource:
    username: root
    password: your_password
```

Or pass as environment variables:

```bash
export DB_PASSWORD=your_password
```

### 3. Build

```bash
mvn clean install -DskipTests
```

### 4. Run

```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`. Flyway creates all tables automatically.

### 5. Run tests

```bash
mvn test
```

Tests use H2 in-memory — no MySQL needed.

---

## Docker Setup

```bash
docker-compose up --build
```

This starts MySQL and the application together. App available at `http://localhost:8080`.

---

## Swagger UI

Open: **http://localhost:8080/swagger-ui.html**

To authenticate:

1. Call `POST /api/auth/admin/login` with `{"username":"admin","password":"Admin@123"}`
2. Copy the `accessToken`
3. Click **Authorize** → enter `Bearer <token>`

---

## API Quick Reference

### Auth (no token needed)

```
POST /api/auth/admin/login        Admin login → JWT
POST /api/auth/student/login      Student login (studentCode + dob) → JWT
```

### Admin endpoints (ROLE_ADMIN)

```
POST   /api/admin/students                     Create student
PUT    /api/admin/students/{id}                Update student
DELETE /api/admin/students/{id}                Delete student
GET    /api/admin/students                     List all (paginated)
GET    /api/admin/students/{id}                Get by ID
GET    /api/admin/students/search?name=        Search by name
GET    /api/admin/courses/{id}/students        Students enrolled in course
POST   /api/admin/course-assignments           Assign courses to student

POST   /api/admin/courses                      Create course
PUT    /api/admin/courses/{id}                 Update course
DELETE /api/admin/courses/{id}                 Delete course
GET    /api/admin/courses                      List all (paginated)
GET    /api/admin/courses/{id}                 Get by ID
```

### Student endpoints (ROLE_STUDENT)

```
PUT    /api/student/profile                    Update own profile
GET    /api/student/courses                    View enrolled courses
GET    /api/student/topics                     View all topics from courses
DELETE /api/student/courses/{courseId}         Leave a course
```

---

## cURL Examples

### Admin login

```bash
curl -s -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123"}' | python3 -m json.tool

# Save the token
TOKEN="paste_token_here"
```

### Create a student

```bash
curl -s -X POST http://localhost:8080/api/admin/students \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentCode": "STU001",
    "name": "Rahul Sharma",
    "dob": "2000-05-15",
    "gender": "MALE",
    "email": "rahul@example.com",
    "mobile": "9876543210",
    "fatherName": "Rajesh Sharma",
    "motherName": "Sunita Sharma",
    "addresses": [
      {
        "type": "PERMANENT",
        "street": "123 MG Road",
        "city": "Bengaluru",
        "state": "Karnataka",
        "country": "India",
        "pincode": "560001"
      }
    ]
  }' | python3 -m json.tool
```

### Create a course

```bash
curl -s -X POST http://localhost:8080/api/admin/courses \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseName": "Java Spring Boot",
    "description": "Enterprise Java with Spring Boot",
    "courseType": "Technical",
    "duration": "3 months",
    "topics": "Spring Boot, JPA, REST APIs, Security, Microservices"
  }' | python3 -m json.tool
```

### Assign course to student

```bash
curl -s -X POST http://localhost:8080/api/admin/course-assignments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"studentId": 1, "courseIds": [1]}' | python3 -m json.tool
```

### Student login

```bash
curl -s -X POST http://localhost:8080/api/auth/student/login \
  -H "Content-Type: application/json" \
  -d '{"studentCode": "STU001", "dateOfBirth": "2000-05-15"}' | python3 -m json.tool

STUDENT_TOKEN="paste_student_token_here"
```

### Student views enrolled courses

```bash
curl -s http://localhost:8080/api/student/courses \
  -H "Authorization: Bearer $STUDENT_TOKEN" | python3 -m json.tool
```

### Student views topics

```bash
curl -s http://localhost:8080/api/student/topics \
  -H "Authorization: Bearer $STUDENT_TOKEN" | python3 -m json.tool
```

### Student leaves a course

```bash
curl -s -X DELETE http://localhost:8080/api/student/courses/1 \
  -H "Authorization: Bearer $STUDENT_TOKEN" | python3 -m json.tool
```

### Get all students (paginated + sorted)

```bash
curl -s "http://localhost:8080/api/admin/students?page=0&size=10&sortBy=name&sortDir=asc" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

### Search students by name

```bash
curl -s "http://localhost:8080/api/admin/students/search?name=Rahul" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/example/sms/
│   │   ├── StudentManagementSystemApplication.java
│   │   ├── config/          SecurityConfig, JpaConfig, OpenApiConfig
│   │   ├── controller/      AuthController, AdminStudentController,
│   │   │                    AdminCourseController, StudentController
│   │   ├── dto/
│   │   │   ├── request/     request DTOs with validation
│   │   │   └── response/    response DTOs
│   │   ├── entity/          Student, Course, Address, User
│   │   ├── exception/       custom exceptions + GlobalExceptionHandler
│   │   ├── mapper/          MapStruct mappers
│   │   ├── repository/      Spring Data JPA repositories
│   │   ├── security/        JwtUtil, JwtAuthenticationFilter, UserDetailsService
│   │   └── service/         interfaces + implementations
│   └── resources/
│       ├── application.yml
│       └── db/migration/    Flyway scripts V1–V5
└── test/
    ├── java/com/example/sms/
    │   ├── controller/      MockMvc tests
    │   └── service/         Mockito unit tests
    └── resources/
        └── application-test.yml
```

---

## Troubleshooting

| Problem                                     | Fix                                              |
|---------------------------------------------|--------------------------------------------------|
| `Access denied for user 'root'@'localhost'` | Update password in `application.yml`             |
| `Unknown database 'sms_db'`                 | Run `CREATE DATABASE sms_db;`                    |
| Port 8080 already in use                    | Change `server.port` in `application.yml`        |
| 401 on API calls                            | Token expired — re-login                         |
| 403 Forbidden                               | Wrong role — use admin token for `/api/admin/**` |
