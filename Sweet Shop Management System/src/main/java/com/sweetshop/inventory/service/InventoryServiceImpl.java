package com.sweetshop.inventory.service;

import com.sweetshop.common.mapper.InventoryMapper;
import com.sweetshop.common.util.RoleChecker;
import com.sweetshop.common.util.ValidationUtil;
import com.sweetshop.exception.BadRequestException;
import com.sweetshop.exception.ResourceNotFoundException;
import com.sweetshop.inventory.dto.InventoryRequest;
import com.sweetshop.inventory.dto.InventoryResponse;
import com.sweetshop.inventory.repository.InventoryRepository;
import com.sweetshop.sweet.domain.Sweet;
import com.sweetshop.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class InventoryServiceImpl implements InventoryService {
    
    private static final String SWEET_RESOURCE_NAME = "Sweet";
    
    private final InventoryRepository inventoryRepository;
    
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    @Override
    public InventoryResponse purchase(InventoryRequest request, User currentUser) {
        ValidationUtil.validateQuantityGreaterThanZero(request.getQuantity(), "Purchase");
        
        Sweet sweet = inventoryRepository.findByIdWithLock(request.getSweetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    SWEET_RESOURCE_NAME + " not found with id: " + request.getSweetId()
                ));
        
        int requestedQuantity = request.getQuantity();
        int availableQuantity = sweet.getQuantity();
        
        if (requestedQuantity > availableQuantity) {
            throw new BadRequestException(
                String.format("Insufficient quantity available. Available: %d, Requested: %d",
                    availableQuantity, requestedQuantity)
            );
        }
        
        int rowsUpdated = inventoryRepository.decreaseQuantity(
            request.getSweetId(), requestedQuantity
        );
        
        if (rowsUpdated == 0) {
            throw new BadRequestException(
                "Purchase failed. Insufficient quantity available."
            );
        }
        
        sweet.setQuantity(availableQuantity - requestedQuantity);
        return InventoryMapper.toResponse(inventoryRepository.save(sweet));
    }
    
    @Override
    public InventoryResponse restock(InventoryRequest request, User currentUser) {
        RoleChecker.requireAdmin(currentUser, "restock inventory");
        ValidationUtil.validateQuantityGreaterThanZero(request.getQuantity(), "Restock");
        
        Sweet sweet = inventoryRepository.findByIdWithLock(request.getSweetId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    SWEET_RESOURCE_NAME + " not found with id: " + request.getSweetId()
                ));
        
        int restockQuantity = request.getQuantity();
        int rowsUpdated = inventoryRepository.increaseQuantity(
            request.getSweetId(), restockQuantity
        );
        
        if (rowsUpdated == 0) {
            throw new ResourceNotFoundException(
                SWEET_RESOURCE_NAME + " not found with id: " + request.getSweetId()
            );
        }
        
        sweet.setQuantity(sweet.getQuantity() + restockQuantity);
        return InventoryMapper.toResponse(inventoryRepository.save(sweet));
    }
}

