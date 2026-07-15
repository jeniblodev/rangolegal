package com.food.rangolegal.infrastructure.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.rangolegal.infrastructure.repository.RestaurantRepository;
import com.food.rangolegal.infrastructure.repository.UserRepository;
import com.food.rangolegal.infrastructure.repository.UserTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestaurantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @BeforeEach
    void cleanDatabaseBeforeEach() {
        cleanDatabase();
    }

    @AfterEach
    void cleanDatabaseAfterEach() {
        cleanDatabase();
    }

    @Test
    void createRestaurant_whenOwnerHasDonoType_returnsCreated() throws Exception {
        Long ownerId = createOwnerWithType("Dono de Restaurante");

        JsonNode restaurant = createRestaurant(ownerId, "Rango Legal");

        assertTrue(restaurant.path("id").asLong() > 0);
        assertEquals("Rango Legal", restaurant.path("name").asText());
        assertEquals("Brasileira", restaurant.path("cuisineType").asText());
        assertEquals("10h as 22h", restaurant.path("operatingHours").asText());
        assertEquals(ownerId.longValue(), restaurant.path("owner").path("id").asLong());
    }

    @Test
    void listAndFindRestaurant_whenExists_returnsPersistedRestaurant() throws Exception {
        Long ownerId = createOwnerWithType("Dono de Restaurante");
        JsonNode created = createRestaurant(ownerId, "Rango Legal");
        Long restaurantId = created.path("id").asLong();

        JsonNode listedRestaurants = getJson("/v1/restaurant");
        assertTrue(containsRestaurantWithId(listedRestaurants, restaurantId));

        JsonNode foundById = getJson("/v1/restaurant/{id}", restaurantId);
        assertEquals(restaurantId.longValue(), foundById.path("id").asLong());
        assertEquals("Rango Legal", foundById.path("name").asText());

        JsonNode foundByName = getJson("/v1/restaurant?name=Rango");
        assertTrue(containsRestaurantWithId(foundByName, restaurantId));
    }

    @Test
    void updateRestaurant_whenValid_returnsUpdatedData() throws Exception {
        Long ownerId = createOwnerWithType("Dono de Restaurante");
        JsonNode created = createRestaurant(ownerId, "Rango Legal");
        Long restaurantId = created.path("id").asLong();

        JsonNode updated = updateRestaurant(restaurantId, ownerId);

        assertUpdatedRestaurant(updated, restaurantId, ownerId);

        JsonNode foundById = getJson("/v1/restaurant/{id}", restaurantId);
        assertUpdatedRestaurant(foundById, restaurantId, ownerId);
    }

    @Test
    void createRestaurant_whenOwnerDoesNotExist_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(restaurantPayload("Rango Legal", 999999L))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Dono do restaurante")));
    }

    @Test
    void createRestaurant_whenUserIsNotOwnerType_returnsBadRequest() throws Exception {
        Long userId = createOwnerWithType("Cliente");

        mockMvc.perform(post("/v1/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(restaurantPayload("Rango Legal", userId))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("nao possui tipo")));
    }

    @Test
    void deleteRestaurant_whenExists_removesAndSubsequentGetFails() throws Exception {
        Long ownerId = createOwnerWithType("Dono de Restaurante");
        JsonNode created = createRestaurant(ownerId, "Rango Legal");
        Long restaurantId = created.path("id").asLong();

        mockMvc.perform(delete("/v1/restaurant/{id}", restaurantId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/restaurant/{id}", restaurantId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Restaurante nao encontrado")));
    }

    private void cleanDatabase() {
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        userTypeRepository.deleteAll();
    }

    private Long createOwnerWithType(String typeName) throws Exception {
        Long userTypeId = createUserType(typeName);
        Long userId = createUser();
        associateUserType(userId, userTypeId);
        return userId;
    }

    private Long createUserType(String name) throws Exception {
        String response = mockMvc.perform(post("/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("name", name))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).path("id").asLong();
    }

    private Long createUser() throws Exception {
        String unique = UUID.randomUUID().toString();

        Map<String, Object> payload = Map.of(
                "name", "Usuario " + unique,
                "email", "usuario-" + unique + "@example.com",
                "login", "usuario-" + unique,
                "password", "senha123",
                "address", addressPayload("Rua A", "100", "01000-000"),
                "userType", "RESTAURANT_OWNER");

        String response = mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(payload)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).path("id").asLong();
    }

    private void associateUserType(Long userId, Long userTypeId) throws Exception {
        mockMvc.perform(patch("/v1/users/{id}/user-type/{userTypeId}", userId, userTypeId))
                .andExpect(status().isOk());
    }

    private JsonNode createRestaurant(Long ownerId, String name) throws Exception {
        String response = mockMvc.perform(post("/v1/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(restaurantPayload(name, ownerId))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response);
    }

    private JsonNode updateRestaurant(Long restaurantId, Long ownerId) throws Exception {
        String response = mockMvc.perform(patch("/v1/restaurant/{id}/data", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatedRestaurantPayload(ownerId))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response);
    }

    private JsonNode getJson(String urlTemplate, Object... uriVariables) throws Exception {
        String response = mockMvc.perform(get(urlTemplate, uriVariables))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response);
    }

    private Map<String, Object> restaurantPayload(String name, Long ownerId) {
        return Map.of(
                "name", name,
                "address", addressPayload("Rua A", "100", "01000-000"),
                "cuisineType", "Brasileira",
                "operatingHours", "10h as 22h",
                "ownerId", ownerId);
    }

    private Map<String, Object> updatedRestaurantPayload(Long ownerId) {
        return Map.of(
                "name", "Rango Atualizado",
                "address", addressPayload("Rua Nova", "200", "02000-000"),
                "cuisineType", "Japonesa",
                "operatingHours", "11h as 23h",
                "ownerId", ownerId);
    }

    private Map<String, Object> addressPayload(String street, String number, String zipCode) {
        return Map.of(
                "street", street,
                "number", number,
                "city", "Sao Paulo",
                "zipCode", zipCode);
    }

    private boolean containsRestaurantWithId(JsonNode restaurants, Long restaurantId) {
        for (JsonNode restaurant : restaurants) {
            if (restaurant.path("id").asLong() == restaurantId.longValue()) {
                return true;
            }
        }
        return false;
    }

    private void assertUpdatedRestaurant(JsonNode restaurant, Long restaurantId, Long ownerId) {
        assertEquals(restaurantId.longValue(), restaurant.path("id").asLong());
        assertEquals("Rango Atualizado", restaurant.path("name").asText());
        assertEquals("Japonesa", restaurant.path("cuisineType").asText());
        assertEquals("11h as 23h", restaurant.path("operatingHours").asText());
        assertEquals("Rua Nova", restaurant.path("address").path("street").asText());
        assertEquals("200", restaurant.path("address").path("number").asText());
        assertEquals("02000-000", restaurant.path("address").path("zipCode").asText());
        assertEquals(ownerId.longValue(), restaurant.path("owner").path("id").asLong());
    }

    private String toJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
