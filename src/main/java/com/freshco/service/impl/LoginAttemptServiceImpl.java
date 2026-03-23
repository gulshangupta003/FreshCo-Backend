package com.freshco.service.impl;

import com.freshco.entity.User;
import com.freshco.exception.BadRequestException;
import com.freshco.repository.UserRepository;
import com.freshco.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final UserRepository userRepository;

    @Value("${app.login.max-attempts}")
    private int maxAttempt;

    @Value("${app.login.lock-duration-minutes}")
    private int lockDurationMinutes;

    @Override
    public void checkAccountLock(User user) {
        if (user.getLockedUntil() == null) {
            return;
        }

        if (LocalDateTime.now().isBefore(user.getLockedUntil())) {
            long minutesLeft = ChronoUnit.MINUTES.between(LocalDateTime.now(), user.getLockedUntil()) + 1;
            log.warn("Login blocked for locked account, user id: {}", user.getId());
            throw new BadRequestException("Account is locked due to too many failed attempts. Try again in "
                    + minutesLeft + " minutes"
            );
        }

        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
        log.warn("Account lock expired, reset for user id: {}", user.getId());
    }

    @Override
    public void handleFailedAttempt(User user) {
        int newFailedAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailedAttempts);

        if (newFailedAttempts >= maxAttempt) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
            userRepository.save(user);
            log.warn("Account locked for user id: {} after {} failed attempts", user.getId(), newFailedAttempts);
            throw new BadRequestException("Account locked due to " + maxAttempt + " failed attempts. Try again in "
                    + lockDurationMinutes + " minutes");
        }

        userRepository.save(user);
        int attemptsLeft = maxAttempt - newFailedAttempts;
        log.warn("Failed login attempt {} for user id: {}, {} attempts remaining",
                newFailedAttempts, user.getId(), attemptsLeft);
    }

    @Override
    public void resetFailedAttempts(User user) {
        if (user.getFailedAttempts() > 0 || user.getLockedUntil() != null) {
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
            log.info("Failed attempts reset for user id: {}", user.getId());
        }
    }

}
