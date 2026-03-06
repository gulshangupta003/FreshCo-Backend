package com.freshco.service;

import com.freshco.dto.request.AddressRequestDto;
import com.freshco.dto.response.AddressResponseDto;

import java.util.List;

public interface AddressService {

    AddressResponseDto createAddress(AddressRequestDto request, Long userId);

    List<AddressResponseDto> getMyAddresses(Long userId);

}
