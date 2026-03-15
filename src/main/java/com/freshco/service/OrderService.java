package com.freshco.service;

import com.freshco.dto.request.PlaceOrderRequestDto;
import com.freshco.dto.response.OrderCountResponseDto;
import com.freshco.dto.response.OrderResponseDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto placeOrder(PlaceOrderRequestDto request, Long userId);

    OrderResponseDto getOrderById(Long orderId, Long userId);

    List<OrderResponseDto> getMyOrders(Long customerId);

    List<OrderResponseDto> getShopOrders(Long shopId, Long sellerId);

    OrderResponseDto updateOrderStatus(Long orderId, String status, Long sellerId);

    OrderResponseDto cancelOrder(Long orderId, Long userId);

    OrderCountResponseDto getShopOrderCount(Long sellerId);

}
