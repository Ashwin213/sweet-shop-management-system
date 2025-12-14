package com.sweetshop.sweet.repository;

import com.sweetshop.sweet.domain.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface SweetRepository extends JpaRepository<Sweet, Long> {
    List<Sweet> findByNameContainingIgnoreCase(String name);
    List<Sweet> findByCategoryIgnoreCase(String category);
    List<Sweet> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
}


