package com.sweetshop.inventory.service;

import com.sweetshop.exception.BadRequestException;
import com.sweetshop.exception.ResourceNotFoundException;
import com.sweetshop.exception.UnauthorizedException;
import com.sweetshop.inventory.dto.InventoryRequest;
import com.sweetshop.inventory.dto.InventoryResponse;
import com.sweetshop.inventory.repository.InventoryRepository;
import com.sweetshop.sweet.domain.Sweet;
import com.sweetshop.user.domain.Role;
import com.sweetshop.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Service Tests")
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryRequest purchaseRequest;
    private InventoryRequest restockRequest;
    private Sweet existingSweet;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        purchaseRequest = new InventoryRequest();
        purchaseRequest.setSweetId(1L);
        purchaseRequest.setQuantity(5);

        restockRequest = new InventoryRequest();
        restockRequest.setSweetId(1L);
        restockRequest.setQuantity(50);

        existingSweet = new Sweet();
        existingSweet.setId(1L);
        existingSweet.setName("Gulab Jamun");
        existingSweet.setCategory("Indian");
        existingSweet.setPrice(new BigDecimal("50.00"));
        existingSweet.setQuantity(100);

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setUsername("user");
        regularUser.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should decrease quantity when purchase is successful")
    void shouldDecreaseQuantityWhenPurchaseIsSuccessful() {
        // Given
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
        when(inventoryRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InventoryResponse response = inventoryService.purchase(purchaseRequest, regularUser);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getSweetId());
        assertEquals("Gulab Jamun", response.getSweetName());
        assertEquals(95, response.getQuantity());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when purchase quantity is zero")
    void shouldThrowBadRequestExceptionWhenPurchaseQuantityIsZero() {
        // Given
        purchaseRequest.setQuantity(0);

        // When & Then
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> inventoryService.purchase(purchaseRequest, regularUser)
        );

        assertEquals("Purchase quantity must be greater than zero", exception.getMessage());
        verify(inventoryRepository, never()).findById(anyLong());
        verify(inventoryRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when purchase quantity exceeds available quantity")
    void shouldThrowBadRequestExceptionWhenPurchaseQuantityExceedsAvailableQuantity() {
        // Given
        purchaseRequest.setQuantity(150);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

        // When & Then
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> inventoryService.purchase(purchaseRequest, regularUser)
        );

        assertEquals("Insufficient quantity available. Available: 100, Requested: 150", exception.getMessage());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when sweet not found during purchase")
    void shouldThrowResourceNotFoundExceptionWhenSweetNotFoundDuringPurchase() {
        // Given
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());
        purchaseRequest.setSweetId(999L);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> inventoryService.purchase(purchaseRequest, regularUser)
        );

        assertEquals("Sweet not found with id: 999", exception.getMessage());
        verify(inventoryRepository, times(1)).findById(999L);
        verify(inventoryRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should increase quantity when restock is successful")
    void shouldIncreaseQuantityWhenRestockIsSuccessful() {
        // Given
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
        when(inventoryRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InventoryResponse response = inventoryService.restock(restockRequest, adminUser);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getSweetId());
        assertEquals("Gulab Jamun", response.getSweetName());
        assertEquals(150, response.getQuantity());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when regular user tries to restock")
    void shouldThrowUnauthorizedExceptionWhenRegularUserTriesToRestock() {
        // Given
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

        // When & Then
        UnauthorizedException exception = assertThrows(
            UnauthorizedException.class,
            () -> inventoryService.restock(restockRequest, regularUser)
        );

        assertEquals("Only ADMIN users can restock inventory", exception.getMessage());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when restock quantity is zero")
    void shouldThrowBadRequestExceptionWhenRestockQuantityIsZero() {
        // Given
        restockRequest.setQuantity(0);

        // When & Then
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> inventoryService.restock(restockRequest, adminUser)
        );

        assertEquals("Restock quantity must be greater than zero", exception.getMessage());
        verify(inventoryRepository, never()).findById(anyLong());
        verify(inventoryRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when sweet not found during restock")
    void shouldThrowResourceNotFoundExceptionWhenSweetNotFoundDuringRestock() {
        // Given
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());
        restockRequest.setSweetId(999L);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> inventoryService.restock(restockRequest, adminUser)
        );

        assertEquals("Sweet not found with id: 999", exception.getMessage());
        verify(inventoryRepository, times(1)).findById(999L);
        verify(inventoryRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should prevent quantity from going negative during purchase")
    void shouldPreventQuantityFromGoingNegativeDuringPurchase() {
        // Given
        existingSweet.setQuantity(3);
        purchaseRequest.setQuantity(5);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

        // When & Then
        BadRequestException exception = assertThrows(
            BadRequestException.class,
            () -> inventoryService.purchase(purchaseRequest, regularUser)
        );

        assertEquals("Insufficient quantity available. Available: 3, Requested: 5", exception.getMessage());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should allow purchase when quantity exactly matches available")
    void shouldAllowPurchaseWhenQuantityExactlyMatchesAvailable() {
        // Given
        existingSweet.setQuantity(5);
        purchaseRequest.setQuantity(5);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
        when(inventoryRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InventoryResponse response = inventoryService.purchase(purchaseRequest, regularUser);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getQuantity());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should handle multiple restocks correctly")
    void shouldHandleMultipleRestocksCorrectly() {
        // Given
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
        when(inventoryRepository.save(any(Sweet.class))).thenAnswer(invocation -> {
            Sweet saved = invocation.getArgument(0);
            existingSweet.setQuantity(saved.getQuantity());
            return saved;
        });

        // When - First restock
        InventoryResponse firstResponse = inventoryService.restock(restockRequest, adminUser);
        assertEquals(150, firstResponse.getQuantity());

        // When - Second restock
        InventoryResponse secondResponse = inventoryService.restock(restockRequest, adminUser);

        // Then
        assertEquals(200, secondResponse.getQuantity());
        verify(inventoryRepository, times(2)).findById(1L);
        verify(inventoryRepository, times(2)).save(any(Sweet.class));
    }
}



