package com.freshco.service.impl;

import com.freshco.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Freshco - Verify Your Email");
        message.setText(
                "Welcome to FreshCo!\n\n" +
                        "Your verification OTP is:\n\n" +
                        otp + "\n\n" +
                        "This OTP expires in 10 minutes.\n\n" +
                        "If you didn't create an account, please ignore this email."
        );

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetMail(String to, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("FreshCo - Password Reset");
        message.setText(
                "You requested a password reset.\n\n" +
                        "Use this token to reset your password:\n\n" +
                        resetToken + "\n\n" +
                        "This token expires in 15 minutes.\n\n" +
                        "If you didn't request this, please ignore this email."
        );

        mailSender.send(message);
    }

}
