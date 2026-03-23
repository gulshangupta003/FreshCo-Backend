package com.freshco.service;

public interface EmailService {

    void sendOtpEmail(String to, String otp);

    void sendPasswordResetMail(String to, String resetToken);

}
