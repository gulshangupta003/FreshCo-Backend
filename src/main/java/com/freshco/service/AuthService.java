package com.freshco.service;

import com.freshco.dto.request.*;
import com.freshco.dto.response.UserDto;

public interface AuthService {

    UserDto register(RegisterRequestDto request);

    UserDto login(LoginRequestDto request);

    void verifyEmail(VerifyEmailRequestDto request);

    void resendOtp(ResendOtpRequestDto request);

    void forgotPassword(ForgotPasswordRequestDto request);

    void resetPassword(ResetPasswordRequestDto request);

}
