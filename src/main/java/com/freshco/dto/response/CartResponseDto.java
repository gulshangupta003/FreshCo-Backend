package com.freshco.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    private Long id;

    private Long shopId;

    private String shopName;

    private List<CartItemResponseDto> items;

    private int totalItems;

    private BigDecimal totalAmount;

}
