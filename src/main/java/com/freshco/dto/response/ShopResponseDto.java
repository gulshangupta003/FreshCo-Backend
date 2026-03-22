package com.freshco.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponseDto {

    private Long id;
    private String name;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Long ownerId;
    private String ownerName;

}
