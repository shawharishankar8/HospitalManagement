package com.project.HospitalManagement.dto;


import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private Long expiresIn;
    private UserResponse user;

    @Data
    public static class UserResponse {
        private Long id;
        private String username;
    }
}
