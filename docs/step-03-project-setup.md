## Step 03: Enable Login Page & API Endpoint Access

### 🎯 Goal
Add login page support and enable API endpoint access (via Postman or other tools) using default Spring Security credentials.

---

### ✅ What We Added / Changed

#### 1. Removed Old Controller
- **HelloController** → deleted, replaced with two more organized controllers for clarity.

#### 2. New Controllers
- **AuthController** → handles login and registration endpoints
    ```java
    @RestController
    @RequestMapping("/auth")
    public class AuthController {
        // public endpoints
    }
    ```
- **UserController** → handles protected operations
    ```java
    @RestController
    @RequestMapping("/userController")
    public class UserController {
        // endpoints require authentication
    }
    ```
#### 3. Security Configuration
- Enabled browser login form:
    ```java
    http.formLogin(withDefaults());
    ```
- Enabled HTTP Basic authentication for API clients:
    ```java
    http.httpBasic(withDefaults());
    ```
> **Note:** Spring Boot 3 / Spring Security 6 removed automatic import of `withDefaults().` Add this:
```java
import static org.springframework.security.config.Customizer.withDefaults;
```
    
> **Note:** Protected endpoints in `UserController` are accessible only after authentication.

---

### 🔍 Testing
- `/auth` or any public endpoints in **AuthController** → accessible without login  
- `/userController` or any protected endpoints in **UserController** → require login 
    - **Browser:** access via login form
    - **API client (curl/Postman):** use HTTP Basic auth
- After login → access granted to restricted endpoints

---

### 📚 What We Learned
- `formLogin()` provides browser login UI  
- `httpBasic()` allows API testing with username/password  
- Spring Security properties (`spring.security.user.name` / `spring.security.user.password`) can be used for temporary authentication  
- Protected endpoints are inaccessible until authenticated  
- Refactoring controllers helps organize authentication vs protected operations

---

### ❗ Reminder
We are still using temporary credentials defined in `application.properties`:
```
spring.security.user.name= username
spring.security.user.password=password
```

---

**Note**: Custom users, password encoding, and database authentication will be implemented in later steps.