# 🔹 Step 5 – Implement User Authentication (Before JWT)

## 🎯 Goal
To enable login authentication using Spring Security with `AuthenticationManager`, `UserDetailsService`, and our `User` entity implementing `UserDetails`.

---

## ✅ What We Implemented

### 🧱 Components Created / Updated

### 1. `User` Entity (`User.java`)
- Implements `UserDetails` from Spring Security.
- Provides required methods (`getAuthorities()`, `isAccountNonLocked()`, etc.)
- Stores fields: `id`, `username`, `password`, and `role`.

```java
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    // getters and setters

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
```
> **📝 Note: Why We Implemented `UserDetails` Directly in the `User` Entity**
>
> We implemented `UserDetails` directly in the `User` entity because:
>
> - The `UserDetailsServiceImpl` must return an object of type `UserDetails`.
> - The `AuthenticationManager` uses this `UserDetails` object for verification during authentication.
>
> This design makes the entity itself compatible with Spring Security, eliminating the need for conversion.
>
> ---
>
> **🧩 Alternative Approach:**
>
> If you don’t want your entity to implement `UserDetails`, you can build and return a Spring Security `User` object in the `UserDetailsServiceImpl` like this:
>
> ```java
> @Override
> public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
>     com.learning.auth.entity.User user = userRepository.findByUsername(username)
>             .orElseThrow(() -> new UsernameNotFoundException("User not found"));
>
>     return org.springframework.security.core.userdetails.User
>             .withUsername(user.getUsername())
>             .password(user.getPassword())
>             .roles(user.getRole())
>             .build();
> }
> ```
>
> ✅ Both approaches are valid —  
> - Implementing `UserDetails` in the entity is cleaner for small projects.  
> - Returning a custom-built `User` object offers flexibility for more complex authentication models.
---

### 2. `UserDetailsServiceImpl.java`
- Loads user by username from database for authentication.
```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
```

### 3. `SecurityConfig.java`
- Registers `AuthenticationManager` and binds it with `UserDetailsService` and `PasswordEncoder`.
```java
@Bean
public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
            .and()
            .build();
}
```
---

### 🔐 4. `UserServiceImpl.java` (Login Logic)
- Uses `AuthenticationManager` to verify credentials.
- On success, sets the `SecurityContext` (user authenticated for this session).
```java
@Override
public String loginUser(String username, String password) {
    try {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "Login Successful";
    } catch (BadCredentialsException e) {
        return "Incorrect Username or Password";
    } catch (UsernameNotFoundException e) {
        return "User Not Found";
    } catch (Exception e) {
        return "Authentication Failed: " + e.getMessage();
    }
}
```

### 🔐 5. `Usercontroller.java`
- Returns the list of users (protected endpoint that requires authentication).
```java
@RestController
@RequestMapping("/userController")
public class UserController {
	
	@Autowired
	private UserServiceImplementation userServiceImplementation;
	
	@GetMapping("/get_user")
	public List<User> getUserDetails() {
		return userServiceImplementation.getUserList();
	}
}
```
**Note:**
> - Currently returns raw user data including sensitive fields (we’ll later introduce a `UserDTO`).
> - Removed default properties `spring.security.username` and `spring.security.password` — authentication now uses database credentials.

---

### 🧭 Request Flow Diagram

<img src="https://github.com/shyam0880/springboot-jwt-auth-api/blob/main/Image/lrequest.drawio.png?raw=true">

---
## 🧪 Testing & Results
### ✅ Test 1 – Register User

Endpoint: `POST /user/register`
Body:
```json
{
  "username": "john",
  "password": "1234"
}
```

Expected: `User Registered Successfully!`

### ✅ Test 2 – Login with Correct Credentials

Endpoint: `POST /user/login`
Body:
```json
{
  "username": "john",
  "password": "1234"
}
```

Expected: `"Login Successful"`

### 🚫 Test 3 – Login with Wrong Password

Expected: `"Incorrect Username or Password"`

### 🚫 Test 4 – Login with Unknown User

Expected: `"User Not Found"`

✅ **Result:** All tests passed successfully — authentication flow works before adding JWT.

---

## 🧠 What We Learned
1. How `AuthenticationManager` Works:
    - Delegates authentication to `UserDetailsService`.
    - Verifies credentials using the configured `PasswordEncoder`.

2. Why `User` Implements `UserDetails`:
    - Makes the entity directly compatible with Spring Security’s authentication process.

3. Difference Between Login & Authorization:
    - **Login** → Verifies credentials.
    - **Authorization** → Controls access based on roles (coming next).

4. `SecurityContextHolder`:
    - Temporarily stores logged-in user details for the current request/session.

---

## ⚠️ Notes & Warnings
- ⚠️ Make sure passwords are always encoded before saving to DB (BCryptPasswordEncoder).
- ⚠️ AuthenticationManager will throw BadCredentialsException for wrong passwords.
- ⚠️ The current authentication is session-based — not stateless.
    - Next step (JWT) will remove dependency on session.
- 🧰 Make sure SecurityConfig has a proper PasswordEncoder bean:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
---

### 🔐 Outcome
✅ Users can now:
- Register (passwords are securely encoded).
- Login using AuthenticationManager.
- Authenticate through the database instead of in-memory credentials.
- Access restricted endpoints after successful login.

