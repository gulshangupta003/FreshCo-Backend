package com.freshco.service.impl;

import com.freshco.dto.request.LoginRequestDto;
import com.freshco.dto.request.RegisterRequestDto;
import com.freshco.dto.response.UserDto;
import com.freshco.entity.Role;
import com.freshco.entity.User;
import com.freshco.exception.BadRequestException;
import com.freshco.repository.UserRepository;
import com.freshco.security.CustomUserDetails;
import com.freshco.security.JwtService;
import com.freshco.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserDto register(RegisterRequestDto request) {
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin registration is not allowed");
        }

        log.info("Registering user with email: {}", request.getEmail());
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with email: {}", savedUser.getEmail());

        String jwtToken = jwtService.generateToken(email);

        return mapToUserDto(savedUser, jwtToken);
    }

    @Override
    public UserDto login(LoginRequestDto request) {
        log.info("Login attempt for email: {}", request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        if (!(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw new BadRequestException("Authentication failed: Unexpected principal type");
        }

        User user = customUserDetails.getUser();
        log.info("Login successful with email: {}", user.getEmail());

        String jwtToken = jwtService.generateToken(user.getEmail());

        return mapToUserDto(user, jwtToken);
    }

    private UserDto mapToUserDto(User user, String token) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .role(user.getRole())
                .token(token)
                .build();
    }

}
