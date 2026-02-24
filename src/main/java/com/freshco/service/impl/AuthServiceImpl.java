package com.freshco.service.impl;

import com.freshco.dto.LoginRequestDto;
import com.freshco.dto.RegisterRequestDto;
import com.freshco.dto.UserDto;
import com.freshco.entity.Role;
import com.freshco.entity.User;
import com.freshco.repository.UserRepository;
import com.freshco.security.CustomUserDetails;
import com.freshco.security.JwtService;
import com.freshco.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Override
    public UserDto register(RegisterRequestDto request) {
        if (request.getRole() != Role.CUSTOMER) {
            throw new RuntimeException("Only customer registration is allowed.");
        }

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        String jwtToken = jwtService.generateToken(email);

        return UserDto.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .token(jwtToken)
                .build();
    }

    @Override
    public UserDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        if (!(authentication.getPrincipal() instanceof CustomUserDetails customUserDetails)) {
            throw new RuntimeException("Authentication failed: Unexpected principal type");
        }

        User user = customUserDetails.getUser();

        String jwtToken = jwtService.generateToken(user.getEmail());

        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .role(user.getRole())
                .token(jwtToken)
                .build();
    }

}
