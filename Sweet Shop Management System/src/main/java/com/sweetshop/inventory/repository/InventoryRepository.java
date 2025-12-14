package com.sweetshop.inventory.repository;

import com.sweetshop.sweet.domain.Sweet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Sweet, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sweet s WHERE s.id = :id")
    Optional<Sweet> findByIdWithLock(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE Sweet s SET s.quantity = s.quantity - :quantity WHERE s.id = :id AND s.quantity >= :quantity")
    int decreaseQuantity(@Param("id") Long id, @Param("quantity") int quantity);
    
    @Modifying
    @Query("UPDATE Sweet s SET s.quantity = s.quantity + :quantity WHERE s.id = :id")
    int increaseQuantity(@Param("id") Long id, @Param("quantity") int quantity);
}


