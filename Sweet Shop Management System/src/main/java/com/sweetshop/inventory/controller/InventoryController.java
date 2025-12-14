package com.sweetshop.inventory.controller;

import com.sweetshop.common.controller.BaseController;
import com.sweetshop.inventory.dto.InventoryRequest;
import com.sweetshop.inventory.dto.InventoryResponse;
import com.sweetshop.inventory.service.InventoryService;
import com.sweetshop.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sweets")
public class InventoryController extends BaseController {
    
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService, UserRepository userRepository) {
        super(userRepository);
        this.inventoryService = inventoryService;
    }
    
    @PostMapping("/{id}/purchase")
    public ResponseEntity<InventoryResponse> purchase(
            @PathVariable @jakarta.validation.constraints.Positive(message = "ID must be a positive number") Long id,
            @Valid @RequestBody InventoryRequest request) {
        request.setSweetId(id);
        return ResponseEntity.ok(inventoryService.purchase(request, getCurrentUser()));
    }
    
    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryResponse> restock(
            @PathVariable @jakarta.validation.constraints.Positive(message = "ID must be a positive number") Long id,
            @Valid @RequestBody InventoryRequest request) {
        request.setSweetId(id);
        return ResponseEntity.ok(inventoryService.restock(request, getCurrentUser()));
    }
}

