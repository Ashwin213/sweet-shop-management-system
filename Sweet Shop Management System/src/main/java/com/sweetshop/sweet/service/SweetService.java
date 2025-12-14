package com.sweetshop.sweet.service;

import com.sweetshop.sweet.dto.PagedSweetResponse;
import com.sweetshop.sweet.dto.SweetRequest;
import com.sweetshop.sweet.dto.SweetResponse;
import com.sweetshop.user.domain.User;

import java.math.BigDecimal;
import java.util.List;

public interface SweetService {
    SweetResponse createSweet(SweetRequest request, User currentUser);
    List<SweetResponse> getAllSweets();
    PagedSweetResponse getAllSweets(int page, int size, String sortBy, String sortDirection);
    SweetResponse getSweetById(Long id);
    SweetResponse updateSweet(Long id, SweetRequest request, User currentUser);
    void deleteSweet(Long id, User currentUser);
    List<SweetResponse> searchByName(String name);
    List<SweetResponse> searchByCategory(String category);
    List<SweetResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}

