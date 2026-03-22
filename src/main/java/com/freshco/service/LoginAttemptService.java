package com.freshco.service;

import com.freshco.entity.User;

public interface LoginAttemptService {

    void checkAccountLock(User user);

    void handleFailedAttempt(User user);

    void resetFailedAttempts(User user);

}
