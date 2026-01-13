package com.Project.HospitalManagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest
{
    @NotBlank(message = "The name can not be empty")
    @Size(min = 3 , max= 50 , message = "name must be between 3 to 50 letters")
    @Pattern(
            regexp = "^[A-Za-z]+( [A-Za-z]+)*$",
            message="provide a valid name, can only have alphabets"
    )
    private String name ;

    @NotBlank(message = "The name can not be empty")
    @Size(min = 3 , max= 50 , message = "Username must be between 3 to 50 letters")
    @Pattern(
            regexp = "^[a-zA-Z0-9@]+$",
            message="provide a valid name, can only have alphabets and numbers and @"
    )
    private String username ;

    @NotBlank(message = "Password can not be blank")
    @Size(min=8, message = "password needs to be least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = " Password must contain : Minimum of 8 characters,At least 1 uppercase letter,At least 1 lowercase letter,At least 1 number,At least 1 special character"
    )
    private String password;
    private String confirmPassword;
}
