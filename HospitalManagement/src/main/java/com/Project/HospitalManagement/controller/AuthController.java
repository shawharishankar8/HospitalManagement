package com.Project.HospitalManagement.controller;

import com.Project.HospitalManagement.dto.AuthResponse;
import com.Project.HospitalManagement.dto.LoginRequest;
import com.Project.HospitalManagement.dto.RegisterRequest;
import com.Project.HospitalManagement.service.AuthService;
import com.Project.HospitalManagement.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Please enter valid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register( @Valid @RequestBody RegisterRequest request) {
        try {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Passwords do not match");
                return ResponseEntity.badRequest().body(error);
            }

            authService.register(request.getName(), request.getUsername(), request.getPassword());

            Map<String, String> response = new HashMap<>();
            response.put("message", "User created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("Username already exists")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage()); // "Invalid refresh token" or "Refresh token expired"
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                authService.logout(username);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Autowired
    private JwtUtil jwtUtil;
}