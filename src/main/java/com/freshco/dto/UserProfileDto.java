package com.freshco.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobileNumber;

    private int role;

    private LocalDateTime createdAt;

}
