package com.freshco.service.impl;

import com.freshco.dto.RegisterRequestDto;
import com.freshco.dto.RegisterResponseDto;
import com.freshco.entity.Role;
import com.freshco.entity.User;
import com.freshco.repository.UserRepository;
import com.freshco.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponseDto register(RegisterRequestDto request) {
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
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return RegisterResponseDto.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

}
