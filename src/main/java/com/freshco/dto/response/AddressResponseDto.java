package com.freshco.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {

    private Long id;

    private String label;

    private String receiverName;

    private String receiverPhone;

    private String addressLine;

    private String landmark;

    private String city;

    private String state;

    private String pincode;

    private boolean isDefault;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
