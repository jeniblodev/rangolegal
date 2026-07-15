package com.food.rangolegal.application;

import com.food.rangolegal.application.dto.MenuItemRequestDTO;
import com.food.rangolegal.application.service.MenuItemService;
import com.food.rangolegal.domain.model.MenuItem;
import com.food.rangolegal.domain.model.Restaurant;
import com.food.rangolegal.infrastructure.repository.MenuItemRepository;
import com.food.rangolegal.infrastructure.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuItemService service;

    @Test
    void save_whenRestaurantExistsAndNameIsUnique_createsItemWithRestaurant() {
        Restaurant restaurant = restaurant(1L, "Rango Legal");
        MenuItemRequestDTO dto = menuItemDto("Feijoada", true, 1L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.existsByNameAndRestaurantId("Feijoada", 1L)).thenReturn(false);
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> {
            MenuItem item = invocation.getArgument(0);
            item.setId(5L);
            return item;
        });

        MenuItem result = service.save(dto);

        assertEquals(5L, result.getId());
        assertEquals("Feijoada", result.getName());
        assertEquals("Descricao do item", result.getDescription());
        assertEquals(new BigDecimal("29.90"), result.getPrice());
        assertTrue(result.isDineInOnly());
        assertEquals("/foto.jpg", result.getPhotoPath());
        assertSame(restaurant, result.getRestaurant());
    }

    @Test
    void save_whenRestaurantDoesNotExist_throwsRuntimeException() {
        MenuItemRequestDTO dto = menuItemDto("Feijoada", true, 99L);

        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.save(dto));
        verify(menuItemRepository, never()).existsByNameAndRestaurantId(anyString(), anyLong());
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void save_whenDuplicateNameInSameRestaurant_throwsBadRequest() {
        Restaurant restaurant = restaurant(1L, "Rango Legal");
        MenuItemRequestDTO dto = menuItemDto("Feijoada", true, 1L);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.existsByNameAndRestaurantId("Feijoada", 1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.save(dto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void listarTodos_returnsItems() {
        Restaurant restaurant = restaurant(1L, "Rango Legal");
        List<MenuItem> items = List.of(
                menuItem(1L, "Feijoada", restaurant),
                menuItem(2L, "Virado", restaurant));

        when(menuItemRepository.findAll()).thenReturn(items);

        List<MenuItem> result = service.listarTodos();

        assertEquals(items, result);
    }

    @Test
    void getById_whenExists_returnsItem() {
        Restaurant restaurant = restaurant(1L, "Rango Legal");
        MenuItem item = menuItem(1L, "Feijoada", restaurant);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        MenuItem result = service.getById(1L);

        assertSame(item, result);
    }

    @Test
    void getById_whenMissing_throwsRuntimeException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getById(99L));
    }

    @Test
    void findByName_returnsMatchingItems() {
        Restaurant restaurant = restaurant(1L, "Rango Legal");
        List<MenuItem> items = List.of(menuItem(1L, "Feijoada", restaurant));

        when(menuItemRepository.findByNameContainingIgnoreCase("feij")).thenReturn(items);

        List<MenuItem> result = service.findByName("feij");

        assertEquals(items, result);
    }

    @Test
    void updateData_whenExistsAndRestaurantProvided_updatesFieldsAndRestaurant() {
        Restaurant currentRestaurant = restaurant(1L, "Rango Legal");
        Restaurant newRestaurant = restaurant(2L, "Tech Burger");
        MenuItem existing = menuItem(1L, "Feijoada", currentRestaurant);
        MenuItemRequestDTO dto = menuItemDto("Burger", false, 2L);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(menuItemRepository.existsByNameAndRestaurantIdAndIdNot("Burger", 2L, 1L)).thenReturn(false);
        when(restaurantRepository.findById(2L)).thenReturn(Optional.of(newRestaurant));
        when(menuItemRepository.save(existing)).thenReturn(existing);

        MenuItem result = service.updateData(1L, dto);

        assertEquals(1L, result.getId());
        assertEquals("Burger", result.getName());
        assertEquals("Descricao do item", result.getDescription());
        assertEquals(new BigDecimal("29.90"), result.getPrice());
        assertFalse(result.isDineInOnly());
        assertEquals("/foto.jpg", result.getPhotoPath());
        assertSame(newRestaurant, result.getRestaurant());
    }

    @Test
    void updateData_whenItemDoesNotExist_throwsRuntimeException() {
        MenuItemRequestDTO dto = menuItemDto("Burger", false, 1L);

        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateData(99L, dto));
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void updateData_whenRestaurantDoesNotExist_throwsRuntimeException() {
        Restaurant currentRestaurant = restaurant(1L, "Rango Legal");
        MenuItem existing = menuItem(1L, "Feijoada", currentRestaurant);
        MenuItemRequestDTO dto = menuItemDto("Burger", false, 2L);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(menuItemRepository.existsByNameAndRestaurantIdAndIdNot("Burger", 2L, 1L)).thenReturn(false);
        when(restaurantRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateData(1L, dto));
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void updateData_whenDuplicateNameInSameRestaurant_throwsBadRequest() {
        Restaurant restaurant = restaurant(1L, "Rango Legal");
        MenuItem existing = menuItem(1L, "Feijoada", restaurant);
        MenuItemRequestDTO dto = menuItemDto("Virado", true, 1L);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(menuItemRepository.existsByNameAndRestaurantIdAndIdNot("Virado", 1L, 1L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.updateData(1L, dto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    void delete_whenExists_deletesItem() {
        when(menuItemRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(menuItemRepository).deleteById(1L);
    }

    @Test
    void delete_whenMissing_throwsRuntimeException() {
        when(menuItemRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(99L));
        verify(menuItemRepository, never()).deleteById(99L);
    }

    private MenuItemRequestDTO menuItemDto(String name, boolean dineInOnly, Long restaurantId) {
        return new MenuItemRequestDTO(
                name,
                "Descricao do item",
                new BigDecimal("29.90"),
                dineInOnly,
                "/foto.jpg",
                restaurantId);
    }

    private MenuItem menuItem(Long id, String name, Restaurant restaurant) {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setName(name);
        item.setDescription("Descricao antiga");
        item.setPrice(new BigDecimal("19.90"));
        item.setDineInOnly(true);
        item.setPhotoPath("/antiga.jpg");
        item.setRestaurant(restaurant);
        return item;
    }

    private Restaurant restaurant(Long id, String name) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        return restaurant;
    }
}
