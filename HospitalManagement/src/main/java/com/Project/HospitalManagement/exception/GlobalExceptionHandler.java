package com.Project.HospitalManagement.exception;

import com.Project.HospitalManagement.dto.HospitalResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 - Validation errors (@Valid failures)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HospitalResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new HospitalResponse<>(
                        false,
                        "Validation failed",
                        errors
                )
        );
    }


    //401 - Invalid login credentials

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HospitalResponse<Void>> handleBadCredentials(
            BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new HospitalResponse<>(
                        false,
                        "Invalid username or password",
                        null
                )
        );
    }

    /**
     * 401 - Custom runtime auth errors (refresh token invalid / expired)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<HospitalResponse<Void>> handleRuntimeException(
            RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new HospitalResponse<>(
                        false,
                        ex.getMessage(),
                        null
                )
        );
    }

    /**
     * 403 - Access denied (JWT present but not allowed)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HospitalResponse<Void>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new HospitalResponse<>(
                        false,
                        "You are not authorized to access this resource",
                        null
                )
        );
    }

    /**
     * 404 - Entity not found (Hospital ID not found, etc.)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<HospitalResponse<Void>> handleEntityNotFound(
            EntityNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new HospitalResponse<>(
                        false,
                        ex.getMessage(),
                        null
                )
        );
    }

    /**
     * 500 - Any unhandled exception (last safety net)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HospitalResponse<Void>> handleGenericException(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new HospitalResponse<>(
                        false,
                        "Internal server error",
                        null
                )
        );
    }
}

