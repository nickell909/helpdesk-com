package com.helpdesk.controller;

import com.helpdesk.BaseIntegrationTest;
import com.helpdesk.dto.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("User API Tests")
public class UserControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /api/admin/users - получение всех пользователей")
    public void testGetAllUsers_Success() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // admin, operator, user
                .andExpect(jsonPath("$[*].login").exists());
    }

    @Test
    @DisplayName("GET /api/admin/users - доступ запрещен для обычного пользователя")
    public void testGetAllUsers_ForbiddenForUser() throws Exception {
        String token = getUserToken();

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/users/{id} - получение пользователя по ID")
    public void testGetUserById_Success() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/admin/users/" + adminUser.getUserId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.fullName").value("Администратор"))
                .andExpect(jsonPath("$.role.name").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/admin/users - создание нового пользователя")
    public void testCreateUser_Success() throws Exception {
        String token = getAdminToken();

        UserRequest request = new UserRequest();
        request.setLogin("newuser");
        request.setPassword("password123");
        request.setFullName("Новый Пользователь");
        request.setEmail("newuser@test.com");
        request.setRoleId(userRole.getRoleId());

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("newuser"))
                .andExpect(jsonPath("$.fullName").value("Новый Пользователь"))
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.role.name").value("USER"));
    }

    @Test
    @DisplayName("POST /api/admin/users - создание с существующим логином")
    public void testCreateUser_DuplicateLogin() throws Exception {
        String token = getAdminToken();

        UserRequest request = new UserRequest();
        request.setLogin("admin"); // Уже существует
        request.setPassword("password123");
        request.setFullName("Тест");
        request.setEmail("test@test.com");
        request.setRoleId(userRole.getRoleId());

        mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // 409 - логин уже занят
    }

    @Test
    @DisplayName("PUT /api/admin/users/{id} - обновление пользователя")
    public void testUpdateUser_Success() throws Exception {
        String token = getAdminToken();

        UserRequest request = new UserRequest();
        request.setLogin("user");
        request.setPassword(""); // Не меняем пароль
        request.setFullName("Обновленное Имя");
        request.setEmail("user@test.com");
        request.setRoleId(userRole.getRoleId());

        mockMvc.perform(put("/api/admin/users/" + regularUser.getUserId())
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Обновленное Имя"));
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - удаление пользователя")
    public void testDeleteUser_Success() throws Exception {
        // Создаем пользователя для удаления
        String createToken = getAdminToken();
        UserRequest createRequest = new UserRequest();
        createRequest.setLogin("todelete");
        createRequest.setPassword("password");
        createRequest.setFullName("To Delete");
        createRequest.setEmail("delete@test.com");
        createRequest.setRoleId(userRole.getRoleId());

        String createResponse = mockMvc.perform(post("/api/admin/users")
                        .header("Authorization", "Bearer " + createToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long userId = objectMapper.readTree(createResponse).get("userId").asLong();

        // Удаляем
        mockMvc.perform(delete("/api/admin/users/" + userId)
                        .header("Authorization", "Bearer " + createToken))
                .andExpect(status().isNoContent());

        // Проверяем, что удален
        mockMvc.perform(get("/api/admin/users/" + userId)
                        .header("Authorization", "Bearer " + createToken))
                .andExpect(status().isNotFound()); // 404 - пользователь не найден
    }

    @Test
    @DisplayName("GET /api/admin/users/operators - получение списка операторов")
    public void testGetOperators_Success() throws Exception {
        String token = getOperatorToken();

        mockMvc.perform(get("/api/admin/users/operators")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // admin и operator
                .andExpect(jsonPath("$[*].role.name").exists());
    }

    @Test
    @DisplayName("GET /api/admin/users/operators - пользователь не может получить список операторов")
    public void testGetOperators_ForbiddenForUser() throws Exception {
        String token = getUserToken();

        mockMvc.perform(get("/api/admin/users/operators")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
