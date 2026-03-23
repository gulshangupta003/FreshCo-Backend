package com.freshco.service.impl;

import com.freshco.entity.User;
import com.freshco.exception.BadRequestException;
import com.freshco.repository.UserRepository;
import com.freshco.service.EmailService;
import com.freshco.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Value("${app.otp.expiration-minutes}")
    private int otpExpirationMinutes;

    @Override
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

    @Override
    public void sendOtp(User user) {
        String otp = generateOtp();

        user.setOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
        log.info("OTP sent to user id: {}", user.getId());
    }

    @Override
    public void verifyOtp(User user, String otp) {
        if (user.getOtp() == null) {
            throw new BadRequestException("No OTP found. Please request a new one");
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiresAt())) {
            throw new BadRequestException("OTP has expired. Please request a new one");
        }

        if (!user.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        user.setOtp(null);
        user.setOtpExpiresAt(null);
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified for user id: {}", user.getId());
    }

}
