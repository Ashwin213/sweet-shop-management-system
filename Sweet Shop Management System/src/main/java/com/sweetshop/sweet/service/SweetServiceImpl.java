package com.sweetshop.sweet.service;

import com.sweetshop.common.mapper.SweetMapper;
import com.sweetshop.common.util.RepositoryHelper;
import com.sweetshop.common.util.RoleChecker;
import com.sweetshop.common.util.SweetValidator;
import com.sweetshop.sweet.domain.Sweet;
import com.sweetshop.sweet.dto.PagedSweetResponse;
import com.sweetshop.sweet.dto.SweetRequest;
import com.sweetshop.sweet.dto.SweetResponse;
import com.sweetshop.sweet.repository.SweetRepository;
import com.sweetshop.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SweetServiceImpl implements SweetService {
    
    private static final String SWEET_RESOURCE_NAME = "Sweet";
    
    private final SweetRepository sweetRepository;
    
    public SweetServiceImpl(SweetRepository sweetRepository) {
        this.sweetRepository = sweetRepository;
    }
    
    @Override
    public SweetResponse createSweet(SweetRequest request, User currentUser) {
        SweetValidator.validate(request);
        Sweet sweet = SweetMapper.toEntity(request);
        return SweetMapper.toResponse(sweetRepository.save(sweet));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SweetResponse> getAllSweets() {
        return sweetRepository.findAll().stream()
                .map(SweetMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedSweetResponse getAllSweets(int page, int size, String sortBy, String sortDirection) {
        org.springframework.data.domain.Sort sort = createSort(sortBy, sortDirection);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        org.springframework.data.domain.Page<Sweet> sweetPage = sweetRepository.findAll(pageable);
        
        List<SweetResponse> content = sweetPage.getContent().stream()
                .map(SweetMapper::toResponse)
                .collect(Collectors.toList());
        
        return new PagedSweetResponse(
            content,
            sweetPage.getNumber(),
            sweetPage.getSize(),
            sweetPage.getTotalElements(),
            sweetPage.getTotalPages(),
            sweetPage.hasNext(),
            sweetPage.hasPrevious()
        );
    }
    
    private org.springframework.data.domain.Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isEmpty()) {
            return org.springframework.data.domain.Sort.unsorted();
        }
        
        org.springframework.data.domain.Sort.Direction direction = 
            "desc".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.DESC 
                : org.springframework.data.domain.Sort.Direction.ASC;
        
        String validSortBy = validateSortField(sortBy);
        return org.springframework.data.domain.Sort.by(direction, validSortBy);
    }
    
    private String validateSortField(String sortBy) {
        if ("price".equalsIgnoreCase(sortBy) || "name".equalsIgnoreCase(sortBy)) {
            return sortBy.toLowerCase();
        }
        return "id";
    }
    
    @Override
    @Transactional(readOnly = true)
    public SweetResponse getSweetById(Long id) {
        Sweet sweet = RepositoryHelper.findByIdOrThrow(sweetRepository, id, SWEET_RESOURCE_NAME);
        return SweetMapper.toResponse(sweet);
    }
    
    @Override
    public SweetResponse updateSweet(Long id, SweetRequest request, User currentUser) {
        SweetValidator.validate(request);
        Sweet sweet = RepositoryHelper.findByIdOrThrow(sweetRepository, id, SWEET_RESOURCE_NAME);
        SweetMapper.updateEntity(sweet, request);
        return SweetMapper.toResponse(sweetRepository.save(sweet));
    }
    
    @Override
    public void deleteSweet(Long id, User currentUser) {
        RoleChecker.requireAdmin(currentUser, "delete sweets");
        RepositoryHelper.findByIdOrThrow(sweetRepository, id, SWEET_RESOURCE_NAME);
        sweetRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SweetResponse> searchByName(String name) {
        return sweetRepository.findByNameContainingIgnoreCase(name).stream()
                .map(SweetMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SweetResponse> searchByCategory(String category) {
        return sweetRepository.findByCategoryIgnoreCase(category).stream()
                .map(SweetMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SweetResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return sweetRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(SweetMapper::toResponse)
                .collect(Collectors.toList());
    }
}

