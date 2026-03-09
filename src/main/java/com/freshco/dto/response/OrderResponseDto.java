package com.freshco.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;

    private String status;
    private String paymentStatus;
    private String paymentMethod;

    private BigDecimal totalAmount;

    private Long shopId;
    private String shopName;

    private Long addressId;
    private String addressLine;
    private String city;

    private List<OrderItemResponseDto> orderItems;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
