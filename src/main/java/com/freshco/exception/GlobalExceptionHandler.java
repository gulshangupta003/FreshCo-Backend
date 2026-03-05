package com.freshco.exception;

import com.freshco.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 401: delegated from CustomAuthenticationEntryPoint
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException e, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Full authentication is required to access this resource"
        );
        problemDetail.setTitle("Authentication failure");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/authentication"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 403: delegated from CustomAccessDeniedHandler
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action"
        );
        problemDetail.setTitle("Access Denied");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/forbidden"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 400: Validation errors (from @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/validation"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 401: Bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                "Invalid email or password");
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/authentication"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 404: Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 409: Duplicate resource
    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResourceException(DuplicateResourceException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("Duplicate resource");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/conflict"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 400: Malformed request body (invalid JSON, bad enum values)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = "Malformed request body";

        Throwable cause = e.getCause();
        if (cause != null && cause.getCause() instanceof IllegalArgumentException illegalArgumentException) {
            message = illegalArgumentException.getMessage();
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/conflict"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // 400: Bad request
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequestException(BadRequestException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/conflict"));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse(ex.getMessage()));
    }

}
