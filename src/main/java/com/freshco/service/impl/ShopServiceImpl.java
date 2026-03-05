package com.freshco.service.impl;

import com.freshco.dto.ShopRequestDto;
import com.freshco.dto.ShopResponseDto;
import com.freshco.entity.Shop;
import com.freshco.entity.User;
import com.freshco.exception.DuplicateResourceException;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.ShopRepository;
import com.freshco.repository.UserRepository;
import com.freshco.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShopResponseDto createShop(ShopRequestDto request, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "sellerId", sellerId));

        if (shopRepository.existsByOwnerId(seller.getId())) {
            throw new DuplicateResourceException("You already have a shop");
        }

        Shop shop = Shop.builder()
                .name(request.getName())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .imageUrl(request.getImageUrl())
                .owner(seller)
                .build();

        Shop savedShop = shopRepository.save(shop);

        return mapToShopResponseDto(savedShop);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponseDto getShopById(Long id) {
        Shop shop = findShopById(id);

        return mapToShopResponseDto(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopResponseDto> getALlShops() {
        return shopRepository.findAll().stream()
                .map(this::mapToShopResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ShopResponseDto updateShop(Long shopId, ShopRequestDto request, Long sellerId) {
        Shop shop = findShopById(shopId);

        if (!shop.getOwner().getId().equals(sellerId)) {
            throw new AccessDeniedException("You can only update your own shop");
        }

        shop.setName(request.getName().trim());
        shop.setAddressLine(request.getAddressLine());
        shop.setCity(request.getCity().trim());
        shop.setImageUrl(request.getImageUrl());

        Shop updatedShop = shopRepository.save(shop);

        return mapToShopResponseDto(updatedShop);
    }

    private Shop findShopById(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop", "id", shopId));
    }

    private ShopResponseDto mapToShopResponseDto(Shop shop) {
        return ShopResponseDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .addressLine(shop.getAddressLine())
                .city(shop.getCity())
                .imageUrl(shop.getImageUrl())
                .createdAt(shop.getCreatedAt())
                .ownerId(shop.getOwner().getId())
                .build();
    }

}
