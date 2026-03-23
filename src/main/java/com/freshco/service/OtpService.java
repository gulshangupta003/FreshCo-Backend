package com.freshco.service;

import com.freshco.entity.User;

public interface OtpService {

    String generateOtp();

    void sendOtp(User user);

    void verifyOtp(User user, String otp);

}
