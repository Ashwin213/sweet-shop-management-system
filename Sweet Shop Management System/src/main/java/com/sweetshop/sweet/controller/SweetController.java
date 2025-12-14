package com.sweetshop.sweet.controller;

import com.sweetshop.common.controller.BaseController;
import com.sweetshop.sweet.dto.PaginationRequest;
import com.sweetshop.sweet.dto.SearchRequest;
import com.sweetshop.sweet.dto.SweetRequest;
import com.sweetshop.sweet.dto.SweetResponse;
import com.sweetshop.sweet.service.SweetService;
import com.sweetshop.user.domain.User;
import com.sweetshop.user.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sweets")
public class SweetController extends BaseController {
    
    private final SweetService sweetService;
    
    public SweetController(SweetService sweetService, UserRepository userRepository) {
        super(userRepository);
        this.sweetService = sweetService;
    }
    
    @PostMapping
    public ResponseEntity<SweetResponse> createSweet(@Valid @RequestBody SweetRequest request) {
        User currentUser = getCurrentUser();
        SweetResponse response = sweetService.createSweet(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllSweets(@ModelAttribute PaginationRequest pagination) {
        if (pagination.hasPagination()) {
            validatePagination(pagination);
            int pageNum = pagination.getPage() != null ? pagination.getPage() : 0;
            int pageSize = pagination.getSize() != null ? pagination.getSize() : 20;
            String sortField = pagination.getSortBy() != null ? pagination.getSortBy() : "id";
            String sortDir = pagination.getSortDirection() != null ? pagination.getSortDirection() : "asc";
            
            return ResponseEntity.ok(sweetService.getAllSweets(pageNum, pageSize, sortField, sortDir));
        }
        
        return ResponseEntity.ok(sweetService.getAllSweets());
    }
    
    private void validatePagination(PaginationRequest pagination) {
        if (pagination.getPage() != null && pagination.getPage() < 0) {
            throw new IllegalArgumentException("Page number must be 0 or greater");
        }
        if (pagination.getSize() != null) {
            if (pagination.getSize() < 1) {
                throw new IllegalArgumentException("Page size must be at least 1");
            }
            if (pagination.getSize() > 100) {
                throw new IllegalArgumentException("Page size cannot exceed 100");
            }
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SweetResponse> getSweetById(
            @PathVariable @Positive(message = "ID must be a positive number") Long id) {
        return ResponseEntity.ok(sweetService.getSweetById(id));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SweetResponse>> searchSweets(
            @ModelAttribute SearchRequest searchRequest) {
        searchRequest.validate();
        List<SweetResponse> results = determineSearchResults(searchRequest);
        return ResponseEntity.ok(results);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SweetResponse> updateSweet(
            @PathVariable @Positive(message = "ID must be a positive number") Long id,
            @Valid @RequestBody SweetRequest request) {
        User currentUser = getCurrentUser();
        return ResponseEntity.ok(sweetService.updateSweet(id, request, currentUser));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSweet(
            @PathVariable @Positive(message = "ID must be a positive number") Long id) {
        sweetService.deleteSweet(id, getCurrentUser());
        return ResponseEntity.noContent().build();
    }
    
    private List<SweetResponse> determineSearchResults(SearchRequest searchRequest) {
        if (searchRequest.hasName()) {
            return sweetService.searchByName(searchRequest.getName());
        }
        if (searchRequest.hasCategory()) {
            return sweetService.searchByCategory(searchRequest.getCategory());
        }
        if (searchRequest.hasPriceRange()) {
            return sweetService.searchByPriceRange(searchRequest.getMinPrice(), searchRequest.getMaxPrice());
        }
        return sweetService.getAllSweets();
    }
}

