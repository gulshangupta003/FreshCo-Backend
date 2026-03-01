package com.freshco.service;

import com.freshco.dto.request.LoginRequestDto;
import com.freshco.dto.request.RegisterRequestDto;
import com.freshco.dto.response.UserDto;

public interface AuthService {

    UserDto register(RegisterRequestDto request);

    UserDto login(LoginRequestDto request);

}
