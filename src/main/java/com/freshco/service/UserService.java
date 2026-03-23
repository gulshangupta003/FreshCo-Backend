package com.freshco.service;

import com.freshco.dto.response.UserProfileDto;

public interface UserService {

    UserProfileDto getMyProfile(Long userId);

}