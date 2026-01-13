package com.Project.HospitalManagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class HospitalRequest {
    @NotBlank(message = "Hospital field can not be blank")
    @Size(min = 3,max = 50, message = "Hospital name needs to be 3-50 characters")
    @Pattern(
            regexp ="^[A-Za-z ]+$",
            message = "Hospital Name can have Characters and spaces"
    )
    private String hospitalName;

    @NotBlank
    @Size(min = 10, max = 200, message = "must be between 10 to 200 characters")
    private String hospitalAddress;

    @Valid
    @NotNull(message = "First contact details are required")
    private ContactRequest firstContact;


    @Valid
    @NotNull(message = "First contact details are required")
    private ContactRequest secondContact;
}
