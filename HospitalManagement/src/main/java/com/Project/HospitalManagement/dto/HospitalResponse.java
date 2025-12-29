package com.Project.HospitalManagement.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalResponse <T>{
    private boolean success;
    private String message;
    private T data;
}
