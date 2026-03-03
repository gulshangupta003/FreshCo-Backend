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

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShopResponseDto createShop(ShopRequestDto request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", sellerEmail));

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
