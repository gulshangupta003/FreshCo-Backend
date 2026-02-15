package com.freshco.service;

import com.freshco.dto.RegisterRequestDto;
import com.freshco.dto.RegisterResponseDto;

public interface AuthService {

    RegisterResponseDto register(RegisterRequestDto request);

}
