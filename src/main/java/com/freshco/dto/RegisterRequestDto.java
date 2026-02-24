package com.freshco.dto;

import com.freshco.entity.Role;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at-least 6 characters long")
    private String password;

    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid mobile number")
    private String mobileNumber;

    @NotNull(message = "Role is required")
    private Role role;

}
