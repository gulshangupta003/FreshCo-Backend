package com.freshco.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private String unit;

    private String imageUrl;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long shopId;

    private String shopName;

    private Long categoryId;

    private String categoryName;

}
