package com.freshco.service;

import com.freshco.dto.request.PlaceOrderRequestDto;
import com.freshco.dto.response.OrderResponseDto;

public interface OrderService {

    OrderResponseDto placeOrder(PlaceOrderRequestDto request, Long userId);

}
