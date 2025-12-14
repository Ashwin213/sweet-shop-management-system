package com.sweetshop.sweet.service;

import com.sweetshop.exception.ResourceNotFoundException;
import com.sweetshop.exception.UnauthorizedException;
import com.sweetshop.sweet.domain.Sweet;
import com.sweetshop.sweet.dto.PagedSweetResponse;
import com.sweetshop.sweet.dto.SweetRequest;
import com.sweetshop.sweet.dto.SweetResponse;
import com.sweetshop.sweet.repository.SweetRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sweet Service Tests")
class SweetServiceTest {

    @Mock
    private SweetRepository sweetRepository;

    @InjectMocks
    private SweetServiceImpl sweetService;

    private SweetRequest sweetRequest;
    private Sweet existingSweet;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        sweetRequest = new SweetRequest();
        sweetRequest.setName("Gulab Jamun");
        sweetRequest.setCategory("Indian");
        sweetRequest.setPrice(new BigDecimal("50.00"));
        sweetRequest.setQuantity(100);

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
    @DisplayName("Should create sweet successfully")
    void shouldCreateSweetSuccessfully() {
        // Given
        when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> {
            Sweet sweet = invocation.getArgument(0);
            sweet.setId(1L);
            return sweet;
        });

        // When
        SweetResponse response = sweetService.createSweet(sweetRequest, adminUser);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Gulab Jamun", response.getName());
        assertEquals("Indian", response.getCategory());
        assertEquals(new BigDecimal("50.00"), response.getPrice());
        assertEquals(100, response.getQuantity());
        verify(sweetRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should get all sweets successfully")
    void shouldGetAllSweetsSuccessfully() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setCategory("Indian");
        sweet1.setPrice(new BigDecimal("50.00"));
        sweet1.setQuantity(100);

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Rasgulla");
        sweet2.setCategory("Indian");
        sweet2.setPrice(new BigDecimal("40.00"));
        sweet2.setQuantity(80);

        when(sweetRepository.findAll()).thenReturn(Arrays.asList(sweet1, sweet2));

        // When
        List<SweetResponse> responses = sweetService.getAllSweets();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Gulab Jamun", responses.get(0).getName());
        assertEquals("Rasgulla", responses.get(1).getName());
        verify(sweetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get paginated sweets successfully")
    void shouldGetPaginatedSweetsSuccessfully() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setCategory("Indian");
        sweet1.setPrice(new BigDecimal("50.00"));
        sweet1.setQuantity(100);

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Rasgulla");
        sweet2.setCategory("Indian");
        sweet2.setPrice(new BigDecimal("40.00"));
        sweet2.setQuantity(80);

        org.springframework.data.domain.Page<Sweet> page = new org.springframework.data.domain.PageImpl<>(
            Arrays.asList(sweet1, sweet2),
            org.springframework.data.domain.PageRequest.of(0, 2),
            2
        );

        when(sweetRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // When
        PagedSweetResponse response = sweetService.getAllSweets(0, 2, "name", "asc");

        // Then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(2, response.getSize());
        assertEquals(2, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertFalse(response.hasNext());
        assertFalse(response.hasPrevious());
        verify(sweetRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should sort sweets by price ascending")
    void shouldSortSweetsByPriceAscending() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setPrice(new BigDecimal("50.00"));

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Rasgulla");
        sweet2.setPrice(new BigDecimal("40.00"));

        org.springframework.data.domain.Page<Sweet> page = new org.springframework.data.domain.PageImpl<>(
            Arrays.asList(sweet2, sweet1),
            org.springframework.data.domain.PageRequest.of(0, 10, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "price")),
            2
        );

        when(sweetRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // When
        PagedSweetResponse response = sweetService.getAllSweets(0, 10, "price", "asc");

        // Then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(new BigDecimal("40.00"), response.getContent().get(0).getPrice());
        assertEquals(new BigDecimal("50.00"), response.getContent().get(1).getPrice());
        verify(sweetRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should sort sweets by price descending")
    void shouldSortSweetsByPriceDescending() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setPrice(new BigDecimal("50.00"));

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Rasgulla");
        sweet2.setPrice(new BigDecimal("40.00"));

        org.springframework.data.domain.Page<Sweet> page = new org.springframework.data.domain.PageImpl<>(
            Arrays.asList(sweet1, sweet2),
            org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "price")),
            2
        );

        when(sweetRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // When
        PagedSweetResponse response = sweetService.getAllSweets(0, 10, "price", "desc");

        // Then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(new BigDecimal("50.00"), response.getContent().get(0).getPrice());
        assertEquals(new BigDecimal("40.00"), response.getContent().get(1).getPrice());
        verify(sweetRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should sort sweets by name ascending")
    void shouldSortSweetsByNameAscending() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setPrice(new BigDecimal("50.00"));

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Rasgulla");
        sweet2.setPrice(new BigDecimal("40.00"));

        org.springframework.data.domain.Page<Sweet> page = new org.springframework.data.domain.PageImpl<>(
            Arrays.asList(sweet1, sweet2),
            org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name")),
            2
        );

        when(sweetRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // When
        PagedSweetResponse response = sweetService.getAllSweets(0, 10, "name", "asc");

        // Then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals("Gulab Jamun", response.getContent().get(0).getName());
        assertEquals("Rasgulla", response.getContent().get(1).getName());
        verify(sweetRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should use default sort when invalid sort field provided")
    void shouldUseDefaultSortWhenInvalidSortFieldProvided() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");

        org.springframework.data.domain.Page<Sweet> page = new org.springframework.data.domain.PageImpl<>(
            Arrays.asList(sweet1),
            org.springframework.data.domain.PageRequest.of(0, 10,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id")),
            1
        );

        when(sweetRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // When
        PagedSweetResponse response = sweetService.getAllSweets(0, 10, "invalidField", "asc");

        // Then
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(sweetRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should handle pagination with hasNext and hasPrevious correctly")
    void shouldHandlePaginationWithHasNextAndHasPreviousCorrectly() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Sweet 1");

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Sweet 2");

        org.springframework.data.domain.Page<Sweet> page = new org.springframework.data.domain.PageImpl<>(
            Arrays.asList(sweet1, sweet2),
            org.springframework.data.domain.PageRequest.of(0, 2),
            5
        );

        when(sweetRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        // When
        PagedSweetResponse response = sweetService.getAllSweets(0, 2, "id", "asc");

        // Then
        assertNotNull(response);
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPage());
        assertEquals(2, response.getSize());
        assertEquals(5, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertTrue(response.hasNext());
        assertFalse(response.hasPrevious());
        verify(sweetRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    @DisplayName("Should get sweet by id successfully")
    void shouldGetSweetByIdSuccessfully() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

        // When
        SweetResponse response = sweetService.getSweetById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Gulab Jamun", response.getName());
        verify(sweetRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when sweet not found by id")
    void shouldThrowResourceNotFoundExceptionWhenSweetNotFoundById() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> sweetService.getSweetById(999L)
        );

        assertEquals("Sweet not found with id: 999", exception.getMessage());
        verify(sweetRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update sweet successfully")
    void shouldUpdateSweetSuccessfully() {
        // Given
        SweetRequest updateRequest = new SweetRequest();
        updateRequest.setName("Updated Gulab Jamun");
        updateRequest.setCategory("Premium");
        updateRequest.setPrice(new BigDecimal("60.00"));
        updateRequest.setQuantity(150);

        when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
        when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        SweetResponse response = sweetService.updateSweet(1L, updateRequest, adminUser);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Updated Gulab Jamun", response.getName());
        assertEquals("Premium", response.getCategory());
        assertEquals(new BigDecimal("60.00"), response.getPrice());
        assertEquals(150, response.getQuantity());
        verify(sweetRepository, times(1)).findById(1L);
        verify(sweetRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent sweet")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentSweet() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> sweetService.updateSweet(999L, sweetRequest, adminUser)
        );

        assertEquals("Sweet not found with id: 999", exception.getMessage());
        verify(sweetRepository, times(1)).findById(999L);
        verify(sweetRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should delete sweet successfully when user is ADMIN")
    void shouldDeleteSweetSuccessfullyWhenUserIsAdmin() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));
        doNothing().when(sweetRepository).deleteById(1L);

        // When
        sweetService.deleteSweet(1L, adminUser);

        // Then
        verify(sweetRepository, times(1)).findById(1L);
        verify(sweetRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when regular user tries to delete sweet")
    void shouldThrowUnauthorizedExceptionWhenRegularUserTriesToDeleteSweet() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(existingSweet));

        // When & Then
        UnauthorizedException exception = assertThrows(
            UnauthorizedException.class,
            () -> sweetService.deleteSweet(1L, regularUser)
        );

        assertEquals("Only ADMIN users can delete sweets", exception.getMessage());
        verify(sweetRepository, times(1)).findById(1L);
        verify(sweetRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent sweet")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentSweet() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> sweetService.deleteSweet(999L, adminUser)
        );

        assertEquals("Sweet not found with id: 999", exception.getMessage());
        verify(sweetRepository, times(1)).findById(999L);
        verify(sweetRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should search sweets by name successfully")
    void shouldSearchSweetsByNameSuccessfully() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setCategory("Indian");
        sweet1.setPrice(new BigDecimal("50.00"));
        sweet1.setQuantity(100);

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Gulab Jamun Special");
        sweet2.setCategory("Indian");
        sweet2.setPrice(new BigDecimal("60.00"));
        sweet2.setQuantity(50);

        when(sweetRepository.findByNameContainingIgnoreCase("gulab")).thenReturn(Arrays.asList(sweet1, sweet2));

        // When
        List<SweetResponse> responses = sweetService.searchByName("gulab");

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertTrue(responses.stream().allMatch(s -> s.getName().toLowerCase().contains("gulab")));
        verify(sweetRepository, times(1)).findByNameContainingIgnoreCase("gulab");
    }

    @Test
    @DisplayName("Should return empty list when no sweets found by name")
    void shouldReturnEmptyListWhenNoSweetsFoundByName() {
        // Given
        when(sweetRepository.findByNameContainingIgnoreCase("nonexistent")).thenReturn(List.of());

        // When
        List<SweetResponse> responses = sweetService.searchByName("nonexistent");

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(sweetRepository, times(1)).findByNameContainingIgnoreCase("nonexistent");
    }

    @Test
    @DisplayName("Should search sweets by category successfully")
    void shouldSearchSweetsByCategorySuccessfully() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setCategory("Indian");
        sweet1.setPrice(new BigDecimal("50.00"));
        sweet1.setQuantity(100);

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Rasgulla");
        sweet2.setCategory("Indian");
        sweet2.setPrice(new BigDecimal("40.00"));
        sweet2.setQuantity(80);

        when(sweetRepository.findByCategoryIgnoreCase("indian")).thenReturn(Arrays.asList(sweet1, sweet2));

        // When
        List<SweetResponse> responses = sweetService.searchByCategory("indian");

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertTrue(responses.stream().allMatch(s -> s.getCategory().equalsIgnoreCase("indian")));
        verify(sweetRepository, times(1)).findByCategoryIgnoreCase("indian");
    }

    @Test
    @DisplayName("Should return empty list when no sweets found by category")
    void shouldReturnEmptyListWhenNoSweetsFoundByCategory() {
        // Given
        when(sweetRepository.findByCategoryIgnoreCase("western")).thenReturn(List.of());

        // When
        List<SweetResponse> responses = sweetService.searchByCategory("western");

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(sweetRepository, times(1)).findByCategoryIgnoreCase("western");
    }

    @Test
    @DisplayName("Should search sweets by price range successfully")
    void shouldSearchSweetsByPriceRangeSuccessfully() {
        // Given
        Sweet sweet1 = new Sweet();
        sweet1.setId(1L);
        sweet1.setName("Gulab Jamun");
        sweet1.setCategory("Indian");
        sweet1.setPrice(new BigDecimal("50.00"));
        sweet1.setQuantity(100);

        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Rasgulla");
        sweet2.setCategory("Indian");
        sweet2.setPrice(new BigDecimal("40.00"));
        sweet2.setQuantity(80);

        Sweet sweet3 = new Sweet();
        sweet3.setId(3L);
        sweet3.setName("Premium Sweet");
        sweet3.setCategory("Premium");
        sweet3.setPrice(new BigDecimal("100.00"));
        sweet3.setQuantity(20);

        BigDecimal minPrice = new BigDecimal("30.00");
        BigDecimal maxPrice = new BigDecimal("60.00");

        when(sweetRepository.findByPriceBetween(minPrice, maxPrice))
                .thenReturn(Arrays.asList(sweet1, sweet2));

        // When
        List<SweetResponse> responses = sweetService.searchByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertTrue(responses.stream().allMatch(s -> 
            s.getPrice().compareTo(minPrice) >= 0 && s.getPrice().compareTo(maxPrice) <= 0));
        verify(sweetRepository, times(1)).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    @DisplayName("Should return empty list when no sweets found in price range")
    void shouldReturnEmptyListWhenNoSweetsFoundInPriceRange() {
        // Given
        BigDecimal minPrice = new BigDecimal("200.00");
        BigDecimal maxPrice = new BigDecimal("300.00");

        when(sweetRepository.findByPriceBetween(minPrice, maxPrice)).thenReturn(List.of());

        // When
        List<SweetResponse> responses = sweetService.searchByPriceRange(minPrice, maxPrice);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(sweetRepository, times(1)).findByPriceBetween(minPrice, maxPrice);
    }

    @Test
    @DisplayName("Should handle price range with same min and max price")
    void shouldHandlePriceRangeWithSameMinAndMaxPrice() {
        // Given
        BigDecimal price = new BigDecimal("50.00");
        when(sweetRepository.findByPriceBetween(price, price))
                .thenReturn(List.of(existingSweet));

        // When
        List<SweetResponse> responses = sweetService.searchByPriceRange(price, price);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(price, responses.get(0).getPrice());
        verify(sweetRepository, times(1)).findByPriceBetween(price, price);
    }
}

