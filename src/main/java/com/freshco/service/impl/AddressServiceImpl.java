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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .label(request.getLabel().trim())
                .receiverName(request.getReceiverName().trim())
                .receiverPhone(request.getReceiverPhone().trim())
                .addressLine(request.getAddressLine().trim())
                .landmark(request.getLandmark())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .pincode(request.getPincode().trim())
                .isDefault(isFirst)
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);

        return mapToAddressResponseDto(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponseDto> getMyAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToAddressResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long addressId, AddressRequestDto request, Long userId) {
        Address address = findAddressById(addressId);

        if (!address.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only update your own address");
        }

        address.setLabel(request.getLabel().trim());
        address.setReceiverName(request.getReceiverName().trim());
        address.setReceiverPhone(request.getReceiverPhone().trim());
        address.setAddressLine(request.getAddressLine().trim());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPincode(request.getPincode().trim());

        Address updatedAddress = addressRepository.save(address);

        return mapToAddressResponseDto(updatedAddress);
    }

    private Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
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
