package com.freshco.service;

import com.freshco.dto.ShopRequestDto;
import com.freshco.dto.ShopResponseDto;

public interface ShopService {

    ShopResponseDto createShop(ShopRequestDto request, Long sellerId);

    ShopResponseDto getShopById(Long id);

}
