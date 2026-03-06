package com.freshco.service;

import com.freshco.dto.request.AddressRequestDto;
import com.freshco.dto.response.AddressResponseDto;

public interface AddressService {

    AddressResponseDto createAddress(AddressRequestDto request, Long userId);

}
