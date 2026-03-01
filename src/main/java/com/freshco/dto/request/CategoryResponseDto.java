package com.freshco.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    private Long id;

    private String name;

    private String imageUrl;

    private LocalDateTime createdAt;

}
