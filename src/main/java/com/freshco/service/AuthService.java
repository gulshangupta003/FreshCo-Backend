package com.freshco.service;

import com.freshco.dto.LoginRequestDto;
import com.freshco.dto.RegisterRequestDto;
import com.freshco.dto.UserDto;

public interface AuthService {

    UserDto register(RegisterRequestDto request);

    UserDto login(LoginRequestDto request);

}
