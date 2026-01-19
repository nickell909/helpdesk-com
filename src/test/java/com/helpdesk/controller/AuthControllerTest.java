package com.helpdesk.controller;

import com.helpdesk.BaseIntegrationTest;
import com.helpdesk.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Auth API Tests")
public class AuthControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /api/auth/login - успешная аутентификация администратора")
    public void testLoginSuccess_Admin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("admin");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.fullName").value("Администратор"));
    }

    @Test
    @DisplayName("POST /api/auth/login - успешная аутентификация оператора")
    public void testLoginSuccess_Operator() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("operator");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("OPERATOR"));
    }

    @Test
    @DisplayName("POST /api/auth/login - успешная аутентификация пользователя")
    public void testLoginSuccess_User() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("user");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/login - неверный пароль")
    public void testLoginFail_WrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("admin");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - несуществующий пользователь")
    public void testLoginFail_UserNotFound() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("nonexistent");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - пустой логин")
    public void testLoginFail_EmptyLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/auth/login - пустой пароль")
    public void testLoginFail_EmptyPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setLogin("admin");
        request.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}
