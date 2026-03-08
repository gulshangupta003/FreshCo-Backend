package com.freshco.service;

import com.freshco.dto.request.AddToCartRequestDto;
import com.freshco.dto.response.CartResponseDto;

public interface CartService {

    CartResponseDto addToCart(AddToCartRequestDto request, Long userId);

    CartResponseDto getCart(Long userId);

}
