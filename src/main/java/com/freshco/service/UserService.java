package com.freshco.service;

import com.freshco.dto.UserProfileDto;

public interface UserService {

    UserProfileDto getMyProfile(Long userId);

}