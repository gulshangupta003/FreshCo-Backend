package com.freshco.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {

    private Long id;

    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String unit;
    private String imageUrl;

    private int quantity;

    private BigDecimal subtotal;

}
