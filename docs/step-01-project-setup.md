# Spring Boot JWT Auth API

## Step 01: Project Setup & Default Security

### 🎯 Goal
Understand Spring Boot Security’s default behavior and set up the project structure.

---

### 🧱 Steps

1. **Create Spring Boot Project**
   - Used [Spring Initializr](https://start.spring.io/) with the following settings:

| Field | Value |
|-------|-------|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.2.x |
| Group | com.example |
| Artifact | auth |
| Name | springboot-jwt-auth-api |
| Packaging | Jar |
| Java Version | 17 |

2. **Add Dependencies**
   - Spring Web  
   - Spring Security  
   - Spring Data JPA  
   - MySQL Driver  
   - Validation  
   - Lombok  

3. **Database Configuration**
   - In `application.properties` or `application.yml`, add temporary testing database:

```properties
#This below details are use for temperory purpose only(just for login)
spring.datasource.username=root
spring.datasource.password=pass123

#Connecting to MySQL database
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```
 **Note:** The credentials (root / pass123) are temporary for testing. Replace with proper credentials in production.

### Create Test Controller
```
java
Copy code
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Secure World!";
    }
}
```
## Run the App

- Start `AuthApplication.java`
- Access: [http://localhost:8080/hello](http://localhost:8080/hello)
- Default Spring Security credentials:
  - **Username:** user
  - **Password:** auto-generated in console (random each time)

---

### 🔍 Observations

- All endpoints are locked by default.
- Accessing `/hello` without login redirects to the login page.
- Default in-memory user is printed in console on startup.
- JPA is connected to MySQL database for future entity usage.

---

### ✅ Learning

- Spring Security auto-protects all HTTP requests unless overridden.
- Database connection is configured for future JPA entities.
- Temporary test controller and default credentials allow safe testing of login.
- Next step: customize `SecurityConfig` to allow public access to some endpoints and secure others.
