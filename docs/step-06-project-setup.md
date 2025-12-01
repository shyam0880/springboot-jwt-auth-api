# 🔹 Step 6 – Implement JWT Authentication (Stateless Security)

## 🎯 Goal
Transform the session-based authentication into a fully stateless JWT (JSON Web Token) authentication system. Integrate JWT token generation, validation, and filtering to secure REST APIs.

---

## ✅ What We Implemented

### 📦 Dependencies Used (For JWT Implementation)

###  `pom.xml` (Update)
```xml
    <!-- JJWT API: For creating and parsing JWT tokens -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>

    <!-- JJWT Implementation: Required runtime implementation -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- JJWT Jackson: For JSON serialization/deserialization within JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
```
### 📝 Why These Dependencies?
| Dependency | Purpose |
|----------------|----------------|
| jjwt-api	    | Main JWT API for generating, signing, decoding JWTs |
| jjwt-impl	    | Provides the core implementation for token signing & validation |
| jjwt-jackson	| Enables JSON parsing inside JWT payload using Jackson |

### 🧱 Components Created / Updated

### 1. `JwtAuthenticationFilter.java` (New)
- Custom filter extending `OncePerRequestFilter`
- Intercepts all incoming requests
- Extracts JWT token from `Authorization` header
- Validates token and sets authentication in `SecurityContext`

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsServiceImplementation userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

### 2. `LoginResponse.java` (New)
- DTO to return structured login response
- Contains JWT token and status message

```java
public class LoginResponse {
    private String token;
    private String message;
    
    // Constructor, getters and setters
}
```

---

### 3. `SecurityConfig.java` (Updated)
#### Changes Made:
- ✅ Removed `formLogin()` and `httpBasic()` — now using pure JWT
- ✅ Added `.sessionManagement()` with `SessionCreationPolicy.STATELESS`
- ✅ Registered `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`
- ✅ Fixed deprecated `.and()` method in `AuthenticationManager` configuration

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}

@Bean
public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
    builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    return builder.build();
}
```

---

### 4. `UserServiceImplementation.java` (Updated)
#### Changes Made:
- ✅ Injected `JwtUtil` to generate tokens
- ✅ Returns `LoginResponse` instead of plain String
- ✅ Generates JWT token on successful authentication

**Before:**
```java
@Override
public String loginUser(String username, String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return "Login Successful";
}
```

**After:**
```java
@Override
public LoginResponse loginUser(String username, String password) {
    try {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate and return JWT token
        String token = jwtUtil.generateToken(username);
        return new LoginResponse(token, "Login Successful");
    } catch (BadCredentialsException e) {
        return new LoginResponse(null, "Incorrect Username or Password");
    } catch (UsernameNotFoundException e) {
        return new LoginResponse(null, "User Not Found");
    } catch (Exception e) {
        return new LoginResponse(null, "Authentication Failed: " + e.getMessage());
    }
}
```

---

### 5. `AuthController.java` (Updated)
#### Changes Made:
- ✅ Changed from `@RequestParam` to `@RequestBody` (credentials no longer exposed in URL)
- ✅ Returns `LoginResponse` object

**Before:**
```java
@PostMapping("/login")
public String loginUser(@RequestParam String username, @RequestParam String password) {
    return userService.loginUser(username, password);
}
```

**After:**
```java
@PostMapping("/login")
public LoginResponse loginUser(@RequestBody UserDTO userDTO) {
    return userService.loginUser(userDTO.getUsername(), userDTO.getPassword());
}
```

---

### 6. `UserDTO.java` (Updated)
- ✅ Added `password` field for login requests
- ✅ Added getter and setter for password

```java
public class UserDTO {
    private Long id;
    private String username;
    private String password; 
    private String role;
    
    // Getters and setters
}
```

---

### 7. `application.properties` (Updated)
- ✅ Added JWT secret key configuration

```properties
# JWT Configuration
jwt.security=MySecretKeyForJWTTokenGenerationAndValidation12345
```

---

### 8. `JwtUtil.java` (Already Existing - Now Integrated)
- ✅ Now actively used for token generation and validation
- Token validity: 10 hours
- Uses HMAC-SHA256 algorithm

---

## 🧭 JWT Authentication Flow

```
1. User Login
   ↓
2. AuthenticationManager validates credentials
   ↓
3. JwtUtil generates token
   ↓
4. Token returned to client
   ↓
5. Client stores token
   ↓
6. Client sends token in Authorization header (Bearer <token>)
   ↓
7. JwtAuthenticationFilter intercepts request
   ↓
8. Filter extracts & validates token
   ↓
9. Sets authentication in SecurityContext
   ↓
10. Request proceeds to controller
```

---

## 🧪 Testing & Results

### ✅ Test 1 – Register User
**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
  "username": "john",
  "password": "password123",
  "role": "ROLE_USER"
}
```

**Response:**
```
User Registered Successfully!
```

---

### ✅ Test 2 – Login (Get JWT Token)
**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "username": "john",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwiaWF0IjoxNjk...",
  "message": "Login Successful"
}
```

---

### ✅ Test 3 – Access Protected Endpoint Without Token
**Endpoint:** `GET /users`

**Headers:** None

**Expected:** `403 Forbidden` or `401 Unauthorized`

---

### ✅ Test 4 – Access Protected Endpoint With Valid Token
**Endpoint:** `GET /users`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Expected:** Successfully returns user list

---

### 🚫 Test 5 – Access With Invalid/Expired Token
**Expected:** `403 Forbidden` or `401 Unauthorized`

---

## 🔄 Key Changes Summary

| Before (Step 5) | After (Step 6) |
|----------------|----------------|
| ❌ Session-based authentication | ✅ Stateless JWT authentication |
| ❌ Form login & HTTP Basic enabled | ✅ Pure JWT authentication |
| ❌ Login returns "Login Successful" string | ✅ Returns JWT token in JSON |
| ❌ Credentials in URL parameters | ✅ Credentials in request body |
| ❌ JWT code exists but unused | ✅ JWT fully integrated |
| ❌ No token validation filter | ✅ Custom JWT filter validates all requests |

---

## 🧠 What We Learned

1. **JWT vs Session-Based Authentication:**
   - JWT = Stateless (server doesn't store session)
   - Sessions = Stateful (server maintains user state)

2. **How JWT Filter Works:**
   - Runs before Spring Security's authentication filter
   - Extracts token from `Authorization: Bearer <token>` header
   - Validates token signature and expiration
   - Sets authentication in `SecurityContext` for the request

3. **Why Stateless is Better for REST APIs:**
   - Scalable (no server-side session storage)
   - Works across multiple servers/instances
   - Mobile-friendly

4. **JWT Token Structure:**
   - Header (algorithm & token type)
   - Payload (claims: username, expiration, etc.)
   - Signature (HMAC verification)

5. **Security Best Practices Implemented:**
   - Passwords never exposed in URLs
   - JWT secret stored in configuration
   - Tokens expire after 10 hours
   - BCrypt for password hashing

---

## ⚠️ Notes & Warnings

- ⚠️ **JWT Secret Key:** In production, use environment variables instead of hardcoding in `application.properties`
  ```bash
  export JWT_SECRET=your-secret-key
  ```

- ⚠️ **Token Storage:** Client should store JWT securely (e.g., HttpOnly cookies or secure storage)

- ⚠️ **Token Expiration:** Current validity is 10 hours — adjust in `JwtUtil.java` as needed:
  ```java
  private final long TOKEN_VALIDITY = 1000*60*60*10; // 10 hours
  ```

- ⚠️ **HTTPS Required:** Always use HTTPS in production to prevent token interception

- 🧰 **Testing Tools:** Use Postman or cURL to test API endpoints with Bearer tokens

---

## 🔐 Outcome

✅ **Fully functional JWT authentication system:**
- Users register with encrypted passwords
- Login returns JWT token
- Protected endpoints validate JWT
- Stateless authentication (no sessions)
- Secure credential handling
- Role-based authorization ready (using `getAuthorities()`)

---

## 📚 Further Improvements (Future Steps)

- 🔄 Implement token refresh mechanism
- 🔒 Add role-based endpoint security (`@PreAuthorize`)
- 📧 Email verification on registration
- 🚪 Logout (token blacklisting)
- 📊 Audit logging for security events

---

**🎉 JWT Implementation Complete! Your REST API is now production-ready with stateless authentication.**
