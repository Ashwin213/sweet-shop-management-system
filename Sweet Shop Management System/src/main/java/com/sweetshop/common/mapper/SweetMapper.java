package com.sweetshop.common.mapper;

import com.sweetshop.sweet.domain.Sweet;
import com.sweetshop.sweet.dto.SweetRequest;
import com.sweetshop.sweet.dto.SweetResponse;

public class SweetMapper {
    
    private SweetMapper() {
        // Utility class - prevent instantiation
    }
    
    public static Sweet toEntity(SweetRequest request) {
        Sweet sweet = new Sweet();
        sweet.setName(request.getName());
        sweet.setCategory(request.getCategory());
        sweet.setPrice(request.getPrice());
        sweet.setQuantity(request.getQuantity());
        return sweet;
    }
    
    public static void updateEntity(Sweet sweet, SweetRequest request) {
        sweet.setName(request.getName());
        sweet.setCategory(request.getCategory());
        sweet.setPrice(request.getPrice());
        sweet.setQuantity(request.getQuantity());
    }
    
    public static SweetResponse toResponse(Sweet sweet) {
        SweetResponse response = new SweetResponse();
        response.setId(sweet.getId());
        response.setName(sweet.getName());
        response.setCategory(sweet.getCategory());
        response.setPrice(sweet.getPrice());
        response.setQuantity(sweet.getQuantity());
        return response;
    }
}



