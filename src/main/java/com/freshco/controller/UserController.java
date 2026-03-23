package com.freshco.controller;

import com.freshco.dto.response.UserProfileDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "3. User", description = "User profile")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Returns the logged-in user's profile information")
    public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserProfileDto response = userService.getMyProfile(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
