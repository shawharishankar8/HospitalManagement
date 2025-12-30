package com.Project.HospitalManagement.service;

import com.Project.HospitalManagement.dto.AuthResponse;
import com.Project.HospitalManagement.entity.User;
import com.Project.HospitalManagement.repository.UserRepository;
import com.Project.HospitalManagement.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public AuthResponse login(String username, String password) {
        // adding password and username to authentication manager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // stores authenticated user into security context, Role-based authorisation
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // generate access token and refresh token
        String accessToken = jwtUtil.generateToken(username);
        String refreshToken = generateRefreshToken();

        // set refresh token to user element and save it into database
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        // setting values to response
        AuthResponse response = new AuthResponse();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(300l); // 5 minutes in seconds

        // setting user id and password
        AuthResponse.UserResponse userResponse = new AuthResponse.UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        response.setUser(userResponse);

        return response;
    }

    public void register(
            @Valid  String name,
            @Valid String username,
            @Valid  String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }


        User user = new User();
        user.setName(name.trim());
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    // generating new refresh token and access token once expired
    public AuthResponse refreshToken(String refreshToken) {

        // checks if the current refresh token is present in the database
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // check if the refresh token is still valid
        if (user.getRefreshTokenExpiry() == null ||
                user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // generate new token
        String newAccessToken = jwtUtil.generateToken(user.getUsername());

        // create a new refresh token and save it into the database
        String newRefreshToken = generateRefreshToken();
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        // generate new jwt token
        AuthResponse response = new AuthResponse();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(300L);

        // add user id and username
        AuthResponse.UserResponse userResponse = new AuthResponse.UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        response.setUser(userResponse);

        return response;
    }

    public void logout(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }
}