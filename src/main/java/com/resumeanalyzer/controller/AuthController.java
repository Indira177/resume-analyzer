package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.LoginRequest;
import com.resumeanalyzer.dto.RegisterRequest;
import com.resumeanalyzer.entity.User;
import com.resumeanalyzer.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody RegisterRequest registerRequest) {

        Map<String, Object> response = new HashMap<>();

        try {

            // -------------------------------------------------
            // Validate request
            // -------------------------------------------------

            if (registerRequest == null) {

                response.put("success", false);
                response.put("message", "Registration data is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            if (registerRequest.getUsername() == null ||
                    registerRequest.getUsername().trim().isEmpty()) {

                response.put("success", false);
                response.put("message", "Username is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            if (registerRequest.getEmail() == null ||
                    registerRequest.getEmail().trim().isEmpty()) {

                response.put("success", false);
                response.put("message", "Email is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            if (registerRequest.getPassword() == null ||
                    registerRequest.getPassword().isEmpty()) {

                response.put("success", false);
                response.put("message", "Password is required");

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            // -------------------------------------------------
            // Clean input
            // -------------------------------------------------

            String username =
                    registerRequest.getUsername().trim();

            String email =
                    registerRequest.getEmail().trim().toLowerCase();

            String password =
                    registerRequest.getPassword();

            // -------------------------------------------------
            // Validate username
            // -------------------------------------------------

            if (username.length() < 3) {

                response.put("success", false);
                response.put(
                        "message",
                        "Username must contain at least 3 characters"
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            // -------------------------------------------------
            // Validate email
            // -------------------------------------------------

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                response.put("success", false);
                response.put(
                        "message",
                        "Enter a valid email address"
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            // -------------------------------------------------
            // Validate password
            // -------------------------------------------------

            if (password.length() < 8) {

                response.put("success", false);
                response.put(
                        "message",
                        "Password must contain at least 8 characters"
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            // -------------------------------------------------
            // Check duplicate username
            // -------------------------------------------------

            if (userRepository
                    .findByUsername(username)
                    .isPresent()) {

                response.put("success", false);
                response.put(
                        "message",
                        "Username already exists"
                );

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
            }

            // -------------------------------------------------
            // Check duplicate email
            // -------------------------------------------------

            if (userRepository
                    .findByEmail(email)
                    .isPresent()) {

                response.put("success", false);
                response.put(
                        "message",
                        "Email already exists"
                );

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(response);
            }

            // -------------------------------------------------
            // Create new user
            // -------------------------------------------------

            User user = new User();

            user.setUsername(username);
            user.setEmail(email);

            // Never save a plain-text password
            user.setPassword(
                    passwordEncoder.encode(password)
            );

            // -------------------------------------------------
            // Save user
            // -------------------------------------------------

            User savedUser =
                    userRepository.save(user);

            // -------------------------------------------------
            // Registration response
            // -------------------------------------------------

            response.put("success", true);
            response.put(
                    "message",
                    "Registration successful"
            );
            response.put("userId", savedUser.getId());
            response.put(
                    "username",
                    savedUser.getUsername()
            );
            response.put(
                    "email",
                    savedUser.getEmail()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put(
                    "message",
                    "Registration failed"
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse httpResponse) {

        Map<String, Object> response = new HashMap<>();

        try {

            // -------------------------------------------------
            // Validate request
            // -------------------------------------------------

            if (loginRequest == null ||
                    loginRequest.getUsername() == null ||
                    loginRequest.getPassword() == null) {

                response.put("success", false);
                response.put(
                        "message",
                        "Username and password are required"
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
            }

            // -------------------------------------------------
            // Authenticate user
            // -------------------------------------------------

            Authentication authentication =
                    authenticationManager.authenticate(

                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getUsername(),
                                    loginRequest.getPassword()
                            )
                    );

            // -------------------------------------------------
            // Create Security Context
            // -------------------------------------------------

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);

            SecurityContextHolder.setContext(context);

            // -------------------------------------------------
            // Create HTTP Session
            // -------------------------------------------------

            request.getSession(true);

            // -------------------------------------------------
            // Save Security Context in Session
            // -------------------------------------------------

            securityContextRepository.saveContext(
                    context,
                    request,
                    httpResponse
            );

            // -------------------------------------------------
            // Get logged-in user
            // -------------------------------------------------

            User user =
                    userRepository
                            .findByUsername(
                                    loginRequest.getUsername()
                            )
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "User not found"
                                    )
                            );

            // -------------------------------------------------
            // Login response
            // -------------------------------------------------

            response.put("success", true);
            response.put(
                    "message",
                    "Login successful"
            );
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            response.put("success", false);
            response.put(
                    "message",
                    "Invalid username or password"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }
    }

    // =========================================================
    // CURRENT USER
    // =========================================================

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> currentUser(
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        // -----------------------------------------------------
        // Check authentication
        // -----------------------------------------------------

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            response.put("success", false);
            response.put("message", "Not logged in");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        // -----------------------------------------------------
        // Find user
        // -----------------------------------------------------

        String username =
                authentication.getName();

        User user =
                userRepository
                        .findByUsername(username)
                        .orElse(null);

        if (user == null) {

            response.put("success", false);
            response.put("message", "User not found");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(response);
        }

        // -----------------------------------------------------
        // Current user response
        // -----------------------------------------------------

        response.put("success", true);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request,
            HttpSession session) {

        Map<String, Object> response =
                new HashMap<>();

        // -----------------------------------------------------
        // Clear security context
        // -----------------------------------------------------

        SecurityContextHolder.clearContext();

        // -----------------------------------------------------
        // Invalidate session
        // -----------------------------------------------------

        if (session != null) {
            session.invalidate();
        }

        response.put("success", true);
        response.put(
                "message",
                "Logout successful"
        );

        return ResponseEntity.ok(response);
    }
}