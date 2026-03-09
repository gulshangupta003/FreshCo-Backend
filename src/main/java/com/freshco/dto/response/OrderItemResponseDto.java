package com.freshco.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long id;

    private Long productId;
    private String productName;
    private String unit;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}
