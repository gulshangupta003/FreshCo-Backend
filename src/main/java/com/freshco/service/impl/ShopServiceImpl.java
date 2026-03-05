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
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop", "id", id));

        return mapToShopResponseDto(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShopResponseDto> getALlShops() {
        return shopRepository.findAll().stream()
                .map(this::mapToShopResponseDto)
                .toList();
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
