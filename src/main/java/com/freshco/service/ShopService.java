package com.freshco.service;

import com.freshco.dto.request.ShopRequestDto;
import com.freshco.dto.response.ShopResponseDto;

import java.util.List;

public interface ShopService {

    ShopResponseDto createShop(ShopRequestDto request, Long sellerId);

    ShopResponseDto getShopById(Long id);

    List<ShopResponseDto> getAllShops();

    ShopResponseDto updateShop(Long shopId, ShopRequestDto request, Long sellerId);

    void deleteShop(Long shopId, Long sellerId);

}
