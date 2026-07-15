package com.food.rangolegal.application;

import com.food.rangolegal.application.dto.RestaurantRequestDTO;
import com.food.rangolegal.application.service.RestaurantService;
import com.food.rangolegal.domain.model.Address;
import com.food.rangolegal.domain.model.Client;
import com.food.rangolegal.domain.model.Restaurant;
import com.food.rangolegal.domain.model.RestaurantOwner;
import com.food.rangolegal.domain.model.User;
import com.food.rangolegal.domain.model.UserType;
import com.food.rangolegal.infrastructure.repository.RestaurantRepository;
import com.food.rangolegal.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RestaurantServiceTest {

    private final RestaurantRepository repository = mock(RestaurantRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final RestaurantService service = new RestaurantService(repository, userRepository);

    @Test
    void save_whenUserHasDonoDeRestauranteType_createsRestaurantRegardlessOfUserSubclass() {
        User owner = new Client();
        owner.setId(10L);
        owner.setUserType(userType("Dono de Restaurante"));
        RestaurantRequestDTO dto = restaurantDto("Rango Legal", 10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));
        when(repository.save(any(Restaurant.class))).thenAnswer(invocation -> {
            Restaurant restaurant = invocation.getArgument(0);
            restaurant.setId(1L);
            return restaurant;
        });

        Restaurant result = service.save(dto);

        assertEquals(1L, result.getId());
        assertEquals("Rango Legal", result.getName());
        assertEquals("Brasileira", result.getCuisineType());
        assertEquals("10h as 22h", result.getOperatingHours());
        assertSame(dto.address(), result.getAddress());
        assertSame(owner, result.getOwner());
    }

    @Test
    void save_whenOwnerTypeIsRestaurantOwner_createsRestaurantWithOwner() {
        User owner = ownerWithType("RESTAURANT_OWNER");
        RestaurantRequestDTO dto = restaurantDto("Tech Burger", 10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));
        when(repository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Restaurant result = service.save(dto);

        assertEquals("Tech Burger", result.getName());
        assertSame(owner, result.getOwner());
    }

    @Test
    void save_whenOwnerIdDoesNotExist_throwsRuntimeException() {
        RestaurantRequestDTO dto = restaurantDto("Rango Legal", 99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.save(dto));
        verify(repository, never()).save(any(Restaurant.class));
    }

    @Test
    void save_whenOwnerHasNoUserType_throwsRuntimeException() {
        User owner = new RestaurantOwner();
        owner.setId(10L);
        RestaurantRequestDTO dto = restaurantDto("Rango Legal", 10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));

        assertThrows(RuntimeException.class, () -> service.save(dto));
        verify(repository, never()).save(any(Restaurant.class));
    }

    @Test
    void save_whenOwnerTypeIsCliente_throwsRuntimeException() {
        User owner = ownerWithType("Cliente");
        RestaurantRequestDTO dto = restaurantDto("Rango Legal", 10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));

        assertThrows(RuntimeException.class, () -> service.save(dto));
        verify(repository, never()).save(any(Restaurant.class));
    }

    @Test
    void listAll_returnsRestaurants() {
        User owner = ownerWithType("Dono de Restaurante");
        List<Restaurant> restaurants = List.of(
                restaurant(1L, "Rango Legal", owner),
                restaurant(2L, "Tech Burger", owner));

        when(repository.findAll()).thenReturn(restaurants);

        List<Restaurant> result = service.listAll();

        assertEquals(restaurants, result);
    }

    @Test
    void findById_whenExists_returnsRestaurant() {
        User owner = ownerWithType("Dono de Restaurante");
        Restaurant restaurant = restaurant(1L, "Rango Legal", owner);

        when(repository.findById(1L)).thenReturn(Optional.of(restaurant));

        Restaurant result = service.findById(1L);

        assertSame(restaurant, result);
    }

    @Test
    void findById_whenMissing_throwsRuntimeException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(99L));
    }

    @Test
    void findByName_returnsMatchingRestaurants() {
        User owner = ownerWithType("Dono de Restaurante");
        List<Restaurant> restaurants = List.of(restaurant(1L, "Rango Legal", owner));

        when(repository.findByNameContainingIgnoreCase("rango")).thenReturn(restaurants);

        List<Restaurant> result = service.findByName("rango");

        assertEquals(restaurants, result);
    }

    @Test
    void updateData_whenExists_updatesFieldsAndOwner() {
        User currentOwner = ownerWithType("Dono de Restaurante");
        User newOwner = ownerWithType("RESTAURANT_OWNER");
        newOwner.setId(11L);
        Restaurant existing = restaurant(1L, "Nome Antigo", currentOwner);
        RestaurantRequestDTO dto = restaurantDto("Nome Novo", 11L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(11L)).thenReturn(Optional.of(newOwner));
        when(repository.save(existing)).thenReturn(existing);

        Restaurant result = service.updateData(1L, dto);

        assertEquals(1L, result.getId());
        assertEquals("Nome Novo", result.getName());
        assertEquals("Brasileira", result.getCuisineType());
        assertEquals("10h as 22h", result.getOperatingHours());
        assertSame(dto.address(), result.getAddress());
        assertSame(newOwner, result.getOwner());
    }

    @Test
    void updateData_whenRestaurantDoesNotExist_throwsRuntimeException() {
        RestaurantRequestDTO dto = restaurantDto("Nome Novo", 10L);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updateData(99L, dto));
        verifyNoInteractions(userRepository);
        verify(repository, never()).save(any(Restaurant.class));
    }

    @Test
    void delete_whenExists_deletesRestaurant() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_whenMissing_throwsRuntimeException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(99L));
        verify(repository, never()).deleteById(99L);
    }

    private RestaurantRequestDTO restaurantDto(String name, Long ownerId) {
        return new RestaurantRequestDTO(name, address("Rua A"), "Brasileira", "10h as 22h", ownerId);
    }

    private Restaurant restaurant(Long id, String name, User owner) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setAddress(address("Rua B"));
        restaurant.setCuisineType("Italiana");
        restaurant.setOperatingHours("11h as 23h");
        restaurant.setOwner(owner);
        return restaurant;
    }

    private User ownerWithType(String typeName) {
        User owner = "Cliente".equalsIgnoreCase(typeName) ? new Client() : new RestaurantOwner();
        owner.setId(10L);
        owner.setUserType(userType(typeName));
        return owner;
    }

    private UserType userType(String name) {
        UserType userType = new UserType();
        userType.setId(1L);
        userType.setName(name);
        return userType;
    }

    private Address address(String street) {
        Address address = new Address();
        address.setStreet(street);
        address.setNumber("100");
        address.setCity("Sao Paulo");
        address.setZipCode("01000-000");
        return address;
    }
}
