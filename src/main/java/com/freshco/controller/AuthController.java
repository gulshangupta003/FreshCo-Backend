package com.freshco.controller;

import com.freshco.dto.request.LoginRequestDto;
import com.freshco.dto.request.RegisterRequestDto;
import com.freshco.dto.response.UserDto;
import com.freshco.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Auth", description = "Register and Login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new SELLER or CUSTOMER account")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequestDto request) {
        UserDto response = authService.register(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates user and returns JWT token.")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto request) {
        UserDto response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
