package com.Project.HospitalManagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactRequest {

    @NotBlank(message = "Contact name is required")
    @Size(
            min = 3,
            max = 50,
            message = "Contact name must be between 3 and 50 characters"
    )
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Contact name can contain only alphabets and spaces"
    )
    private String name;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Contact number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Contact number must be exactly 10 digits"
    )
    private String contactNumber;
}

