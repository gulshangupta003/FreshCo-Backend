package com.freshco.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Request payload for user login")
public class LoginRequestDto {

    @NotBlank(message = "Email is required")
    @Email
    @Schema(description = "User's registered email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "Password@123")
    private String password;

}
