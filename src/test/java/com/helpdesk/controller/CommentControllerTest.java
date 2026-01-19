package com.helpdesk.controller;

import com.helpdesk.BaseIntegrationTest;
import com.helpdesk.dto.CommentRequest;
import com.helpdesk.entity.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Comment API Tests")
public class CommentControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /api/tickets/{id}/comments - добавление комментария")
    public void testAddComment_Success() throws Exception {
        Ticket ticket = createTicket("Тестовая заявка", regularUser);
        String token = getUserToken();

        CommentRequest request = new CommentRequest();
        request.setContent("Это тестовый комментарий");

        mockMvc.perform(post("/api/tickets/" + ticket.getTicketId() + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Это тестовый комментарий"))
                .andExpect(jsonPath("$.author.login").value("user"))
                .andExpect(jsonPath("$.ticket.ticketId").value(ticket.getTicketId()));
    }

    @Test
    @DisplayName("GET /api/tickets/{id}/comments - получение комментариев")
    public void testGetComments_Success() throws Exception {
        Ticket ticket = createTicket("Тестовая заявка", regularUser);

        // Добавляем комментарий через API
        String token = getUserToken();
        CommentRequest request = new CommentRequest();
        request.setContent("Первый комментарий");

        mockMvc.perform(post("/api/tickets/" + ticket.getTicketId() + "/comments")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)));

        // Получаем комментарии
        mockMvc.perform(get("/api/tickets/" + ticket.getTicketId() + "/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Первый комментарий"));
    }

    @Test
    @DisplayName("POST /api/tickets/{id}/comments - пустой комментарий")
    public void testAddComment_EmptyContent() throws Exception {
        Ticket ticket = createTicket("Тестовая заявка", regularUser);
        String token = getUserToken();

        CommentRequest request = new CommentRequest();
        request.setContent("");

        mockMvc.perform(post("/api/tickets/" + ticket.getTicketId() + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /api/tickets/{id}/comments - комментарий к чужой заявке")
    public void testAddComment_ForbiddenForOtherUser() throws Exception {
        Ticket ticket = createTicket("Заявка оператора", operatorUser);
        String token = getUserToken();

        CommentRequest request = new CommentRequest();
        request.setContent("Попытка прокомментировать чужую заявку");

        mockMvc.perform(post("/api/tickets/" + ticket.getTicketId() + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/tickets/{id}/comments - без авторизации")
    public void testGetComments_Unauthorized() throws Exception {
        Ticket ticket = createTicket("Тестовая заявка", regularUser);

        mockMvc.perform(get("/api/tickets/" + ticket.getTicketId() + "/comments"))
                .andExpect(status().isUnauthorized());
    }

    private Ticket createTicket(String title, com.helpdesk.entity.User createdBy) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription("Описание: " + title);
        ticket.setCategory(softwareCategory);
        ticket.setPriority(mediumPriority);
        ticket.setStatus(newStatus);
        ticket.setCreatedBy(createdBy);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket = ticketRepository.save(ticket);
        ticket.setTicketNumber("TKT-2026-" + String.format("%03d", ticket.getTicketId()));
        return ticketRepository.save(ticket);
    }
}
