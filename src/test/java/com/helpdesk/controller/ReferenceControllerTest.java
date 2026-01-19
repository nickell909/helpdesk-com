package com.helpdesk.controller;

import com.helpdesk.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Reference API Tests")
public class ReferenceControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /api/references/categories - получение категорий")
    public void testGetCategories_Success() throws Exception {
        String token = getUserToken();

        mockMvc.perform(get("/api/references/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // software, hardware
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[*].categoryId").exists());
    }

    @Test
    @DisplayName("GET /api/references/priorities - получение приоритетов")
    public void testGetPriorities_Success() throws Exception {
        String token = getUserToken();

        mockMvc.perform(get("/api/references/priorities")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // low, medium, high
                .andExpect(jsonPath("$[*].name").exists());
    }

    @Test
    @DisplayName("GET /api/references/statuses - получение статусов")
    public void testGetStatuses_Success() throws Exception {
        String token = getUserToken();

        mockMvc.perform(get("/api/references/statuses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4))) // new, in progress, resolved, closed
                .andExpect(jsonPath("$[*].name").exists());
    }

    @Test
    @DisplayName("GET /api/references/roles - получение ролей (только ADMIN)")
    public void testGetRoles_Success() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/references/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // USER, OPERATOR, ADMIN
                .andExpect(jsonPath("$[*].name").exists());
    }

    @Test
    @DisplayName("GET /api/references/roles - запрещено для обычного пользователя")
    public void testGetRoles_ForbiddenForUser() throws Exception {
        String token = getUserToken();

        mockMvc.perform(get("/api/references/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/references/categories - без авторизации")
    public void testGetCategories_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/references/categories"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/references/priorities - оператор может получить")
    public void testGetPriorities_Operator() throws Exception {
        String token = getOperatorToken();

        mockMvc.perform(get("/api/references/priorities")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
}
