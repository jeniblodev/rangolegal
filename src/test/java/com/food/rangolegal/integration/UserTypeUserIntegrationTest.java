package com.food.rangolegal.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.rangolegal.repository.UserRepository;
import com.food.rangolegal.repository.UserTypeRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserTypeUserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
        userTypeRepository.deleteAll();
    }

    @Test
    void updateUserType_whenUserAndTypeExist_associatesTypeWithUser() throws Exception {
        Long userTypeId = createUserType("Dono de Restaurante");

        mockMvc.perform(get("/v1/user-types/{id}", userTypeId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Dono de Restaurante")));

        Long userId = createUser();

        String response = mockMvc.perform(patch("/v1/users/{id}/user-type/{userTypeId}", userId, userTypeId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode associatedUser = objectMapper.readTree(response);
        assertEquals(userId.longValue(), associatedUser.path("id").asLong());
        assertEquals(userTypeId.longValue(), associatedUser.path("userType").path("id").asLong());
        assertEquals("Dono de Restaurante", associatedUser.path("userType").path("name").asText());
    }

    @Test
    void updateUserType_whenUserDoesNotExist_returnsBadRequest() throws Exception {
        Long userTypeId = createUserType("Dono de Restaurante");

        mockMvc.perform(patch("/v1/users/{id}/user-type/{userTypeId}", 999999L, userTypeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Usuario nao encontrado")));
    }

    @Test
    void updateUserType_whenUserTypeDoesNotExist_returnsBadRequest() throws Exception {
        Long userId = createUser();

        mockMvc.perform(patch("/v1/users/{id}/user-type/{userTypeId}", userId, 999999L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Tipo de usuario nao encontrado com o ID: 999999")));
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
                "address", Map.of(
                        "street", "Rua A",
                        "number", "100",
                        "city", "Sao Paulo",
                        "zipCode", "01000-000"),
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

    private String toJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
