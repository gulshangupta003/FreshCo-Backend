package com.freshco.service.impl;

import com.freshco.dto.request.*;
import com.freshco.dto.response.UserDto;
import com.freshco.entity.PasswordResetToken;
import com.freshco.enums.Role;
import com.freshco.entity.User;
import com.freshco.exception.BadRequestException;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.PasswordResetTokenRepository;
import com.freshco.repository.UserRepository;
import com.freshco.security.JwtService;
import com.freshco.service.AuthService;
import com.freshco.service.EmailService;
import com.freshco.service.LoginAttemptService;
import com.freshco.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final OtpService otpService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Value("${app.password-reset.token-expiration-minutes}")
    private int tokenExpirationMinutes;

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
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(email)
                .mobileNumber(request.getMobileNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with email: {}", savedUser.getEmail());

        otpService.sendOtp(savedUser);

        return mapToUserDto(savedUser, jwtService.generateToken(email));
    }

    @Override
    public UserDto login(LoginRequestDto request) {
        String email = request.getEmail().trim().toLowerCase();
        log.info("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isEmailVerified()) {
            throw new BadRequestException("Email not verified. Please verify your email first");
        }

        loginAttemptService.checkAccountLock(user);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (Exception e) {
            loginAttemptService.handleFailedAttempt(user);
            throw new BadRequestException("Invalid email or password");
        }

        loginAttemptService.resetFailedAttempts(user);
        log.info("Login successful for user id: {}", user.getId());

        return mapToUserDto(user, jwtService.generateToken(user.getEmail()));
    }

    @Override
    public void verifyEmail(VerifyEmailRequestDto request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }

        otpService.verifyOtp(user, request.getOtp());
    }

    @Override
    public void resendOtp(ResendOtpRequestDto request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }

        otpService.sendOtp(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDto request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(tokenExpirationMinutes))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetMail(email, token);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDto request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or already used token"));

        if (resetToken.isExpired()) {
            throw new BadRequestException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    private UserDto mapToUserDto(User user, String token) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .token(token)
                .build();
    }

}
