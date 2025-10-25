## Step 02: Public Endpoint & Custom SecurityConfig

### 🎯 Goal
Customize Spring Security to allow specific endpoints to be accessed publicly while keeping others protected.

---

### ✅ What We Configured

- Added a custom `SecurityConfig` class using `SecurityFilterChain`
```java
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {
        
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
            return http
                    .csrf(csrf->csrf.disable())
                    .authorizeHttpRequests(auth->auth
                            .requestMatchers("/hello").permitAll()     //endpoint is permitted
                            .anyRequest().authenticated()              //remaining endpoints are restricted
                            ).build();
            
        }

    }
```
- Temporarily disabled CSRF for easier testing
- Allowed `/hello` endpoint to be accessed without authentication
- Restricted `/security_details` endpoint — authentication required
```Java
  // Inside HelloController
  @GetMapping("/security_details")
      public String securityDetails() {
      	return "File contains security details.";
      }
```
- All other requests remain protected by default

---

### 🔍 Testing Results

| Endpoint | Result |
|---------|-------|
| `/hello` | ✅ Accessible without login |
| `/security_details` | ❌ Shows **Access Denied** (HTTP 403) because no login mechanism is enabled yet |

---

> 🔒 **Note:**  
> - Disabling CSRF is only for testing. We will properly configure CSRF later.  
> - We have **not** enabled `.formLogin()` or `.httpBasic()` yet.  
>   Therefore, Spring Security does not display a login page and rejects access with **403 Forbidden** when hitting protected endpoints.

---

### 📚 What We Learned

- Spring Security behavior can be overridden using `SecurityFilterChain`
- `permitAll()` and `authenticated()` help define public vs. secure APIs
- Since we haven't configured a login method yet, protected routes cannot be accessed
- This lays the foundation for custom authentication and login flow
