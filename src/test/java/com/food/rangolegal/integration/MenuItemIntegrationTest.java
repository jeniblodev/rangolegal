package com.food.rangolegal.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.rangolegal.repository.MenuItemRepository;
import com.food.rangolegal.repository.RestaurantRepository;
import com.food.rangolegal.repository.UserRepository;
import com.food.rangolegal.repository.UserTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class MenuItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MenuItemRepository menuItemRepository;

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
    void createMenuItem_whenRestaurantExists_returnsCreated() throws Exception {
        Long restaurantId = createValidRestaurant();

        JsonNode menuItem = createMenuItem(restaurantId, "Feijoada");

        assertTrue(menuItem.path("id").asLong() > 0);
        assertMenuItem(menuItem, "Feijoada", "Feijoada completa", "29.90", true, "/feijoada.jpg");
        assertFalse(menuItem.has("restaurant"));
    }

    @Test
    void listAndFindMenuItem_whenExists_returnsPersistedItem() throws Exception {
        Long restaurantId = createValidRestaurant();
        JsonNode created = createMenuItem(restaurantId, "Feijoada");
        Long menuItemId = created.path("id").asLong();

        JsonNode listedItems = getJson("/v1/menu_item");
        assertTrue(containsItemWithId(listedItems, menuItemId));

        JsonNode foundById = getJson("/v1/menu_item/{id}", menuItemId);
        assertEquals(menuItemId.longValue(), foundById.path("id").asLong());
        assertEquals("Feijoada", foundById.path("name").asText());

        JsonNode foundByName = getJson("/v1/menu_item?name=Feij");
        assertTrue(containsItemWithId(foundByName, menuItemId));
    }

    @Test
    void updateMenuItem_whenValid_returnsUpdatedData() throws Exception {
        Long restaurantId = createValidRestaurant();
        JsonNode created = createMenuItem(restaurantId, "Feijoada");
        Long menuItemId = created.path("id").asLong();

        JsonNode updated = updateMenuItem(menuItemId, restaurantId);

        assertUpdatedMenuItem(updated, menuItemId);

        JsonNode foundById = getJson("/v1/menu_item/{id}", menuItemId);
        assertUpdatedMenuItem(foundById, menuItemId);
    }

    @Test
    void createMenuItem_whenRestaurantDoesNotExist_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/v1/menu_item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(menuItemPayload("Feijoada", 999999L))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Restaurante")));
    }

    @Test
    void createMenuItem_whenDuplicateNameInSameRestaurant_returnsBadRequest() throws Exception {
        Long restaurantId = createValidRestaurant();

        createMenuItem(restaurantId, "Feijoada");

        mockMvc.perform(post("/v1/menu_item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(menuItemPayload("Feijoada", restaurantId))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMenuItem_whenPriceIsInvalid_returnsBadRequest() throws Exception {
        Long restaurantId = createValidRestaurant();

        mockMvc.perform(post("/v1/menu_item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(menuItemPayload("Feijoada", "0", restaurantId))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMenuItem_whenExists_removesAndSubsequentGetFails() throws Exception {
        Long restaurantId = createValidRestaurant();
        JsonNode created = createMenuItem(restaurantId, "Feijoada");
        Long menuItemId = created.path("id").asLong();

        mockMvc.perform(delete("/v1/menu_item/{id}", menuItemId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/menu_item/{id}", menuItemId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Item do")));
    }

    private void cleanDatabase() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        userTypeRepository.deleteAll();
    }

    private Long createValidRestaurant() throws Exception {
        Long userTypeId = createUserType("Dono de Restaurante");
        Long ownerId = createUser();
        associateUserType(ownerId, userTypeId);

        String response = mockMvc.perform(post("/v1/restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(restaurantPayload(ownerId))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).path("id").asLong();
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

    private JsonNode createMenuItem(Long restaurantId, String name) throws Exception {
        String response = mockMvc.perform(post("/v1/menu_item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(menuItemPayload(name, restaurantId))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response);
    }

    private JsonNode updateMenuItem(Long menuItemId, Long restaurantId) throws Exception {
        String response = mockMvc.perform(patch("/v1/menu_item/{id}/data", menuItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updatedMenuItemPayload(restaurantId))))
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

    private Map<String, Object> restaurantPayload(Long ownerId) {
        return Map.of(
                "name", "Rango Legal " + UUID.randomUUID(),
                "address", addressPayload("Rua A", "100", "01000-000"),
                "cuisineType", "Brasileira",
                "operatingHours", "10h as 22h",
                "ownerId", ownerId);
    }

    private Map<String, Object> menuItemPayload(String name, Long restaurantId) {
        return menuItemPayload(name, "29.90", restaurantId);
    }

    private Map<String, Object> menuItemPayload(String name, String price, Long restaurantId) {
        return Map.of(
                "name", name,
                "description", "Feijoada completa",
                "price", price,
                "dineInOnly", true,
                "photoPath", "/feijoada.jpg",
                "restaurantId", restaurantId);
    }

    private Map<String, Object> updatedMenuItemPayload(Long restaurantId) {
        return Map.of(
                "name", "Burger",
                "description", "Burger artesanal",
                "price", "39.90",
                "dineInOnly", false,
                "photoPath", "/burger.jpg",
                "restaurantId", restaurantId);
    }

    private Map<String, Object> addressPayload(String street, String number, String zipCode) {
        return Map.of(
                "street", street,
                "number", number,
                "city", "Sao Paulo",
                "zipCode", zipCode);
    }

    private boolean containsItemWithId(JsonNode items, Long itemId) {
        for (JsonNode item : items) {
            if (item.path("id").asLong() == itemId.longValue()) {
                return true;
            }
        }
        return false;
    }

    private void assertUpdatedMenuItem(JsonNode menuItem, Long menuItemId) {
        assertEquals(menuItemId.longValue(), menuItem.path("id").asLong());
        assertMenuItem(menuItem, "Burger", "Burger artesanal", "39.90", false, "/burger.jpg");
    }

    private void assertMenuItem(JsonNode menuItem, String name, String description, String price,
                                boolean dineInOnly, String photoPath) {
        assertEquals(name, menuItem.path("name").asText());
        assertEquals(description, menuItem.path("description").asText());
        assertEquals(0, new BigDecimal(price).compareTo(menuItem.path("price").decimalValue()));
        assertEquals(dineInOnly, menuItem.path("dineInOnly").asBoolean());
        assertEquals(photoPath, menuItem.path("photoPath").asText());
    }

    private String toJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
