package com.sweetshop.inventory.service;

import com.sweetshop.inventory.dto.InventoryRequest;
import com.sweetshop.inventory.dto.InventoryResponse;
import com.sweetshop.user.domain.User;

public interface InventoryService {
    InventoryResponse purchase(InventoryRequest request, User currentUser);
    InventoryResponse restock(InventoryRequest request, User currentUser);
}



