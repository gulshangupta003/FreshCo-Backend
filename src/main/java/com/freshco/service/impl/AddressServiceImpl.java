package com.freshco.service.impl;

import com.freshco.dto.request.AddressRequestDto;
import com.freshco.dto.response.AddressResponseDto;
import com.freshco.entity.Address;
import com.freshco.entity.User;
import com.freshco.exception.BadRequestException;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.AddressRepository;
import com.freshco.repository.UserRepository;
import com.freshco.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public AddressResponseDto createAddress(AddressRequestDto request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        long AddressCount = addressRepository.countByUserId(userId);
        if (AddressCount >= 5) {
            throw new BadRequestException(
                    "You can have a maximum of 5 addresses. Delete an existing address to add new one"
            );
        }

        boolean isFirst = AddressCount == 0;

        Address address = Address.builder()
                .label(request.getLabel())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .addressLine(request.getAddressLine())
                .landmark(request.getLandmark())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .isDefault(isFirst)
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);

        return mapToAddressResponseDto(savedAddress);
    }

    private AddressResponseDto mapToAddressResponseDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .label(address.getLabel())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .addressLine(address.getAddressLine())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .isDefault(address.isDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

}
