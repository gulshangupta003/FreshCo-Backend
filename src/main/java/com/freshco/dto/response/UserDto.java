package com.freshco.dto.response;

import com.freshco.enums.Role;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private Role role;
    private String token;
    private boolean emailVerified;
}
