package com.sweetshop.common.mapper;

import com.sweetshop.inventory.dto.InventoryResponse;
import com.sweetshop.sweet.domain.Sweet;

public class InventoryMapper {
    
    private InventoryMapper() {
        // Utility class - prevent instantiation
    }
    
    public static InventoryResponse toResponse(Sweet sweet) {
        InventoryResponse response = new InventoryResponse();
        response.setSweetId(sweet.getId());
        response.setSweetName(sweet.getName());
        response.setQuantity(sweet.getQuantity());
        return response;
    }
}



