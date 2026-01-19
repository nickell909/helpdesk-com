package com.helpdesk.controller;

import com.helpdesk.BaseIntegrationTest;
import com.helpdesk.dto.TicketRequest;
import com.helpdesk.entity.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Ticket API Tests")
public class TicketControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("POST /api/tickets - создание заявки пользователем")
    public void testCreateTicket_Success() throws Exception {
        String token = getUserToken();

        TicketRequest request = new TicketRequest();
        request.setTitle("Не работает принтер");
        request.setDescription("Принтер HP в офисе 301 не печатает");
        request.setCategoryId(hardwareCategory.getCategoryId());
        request.setPriorityId(mediumPriority.getPriorityId());

        mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketNumber").exists())
                .andExpect(jsonPath("$.ticketNumber").value(matchesPattern("TKT-\\d{4}-\\d{3}")))
                .andExpect(jsonPath("$.title").value("Не работает принтер"))
                .andExpect(jsonPath("$.description").value("Принтер HP в офисе 301 не печатает"))
                .andExpect(jsonPath("$.status.name").value("Новая"))
                .andExpect(jsonPath("$.createdBy.login").value("user"))
                .andExpect(jsonPath("$.assignedTo").isEmpty());
    }

    @Test
    @DisplayName("POST /api/tickets - создание заявки без авторизации")
    public void testCreateTicket_Unauthorized() throws Exception {
        TicketRequest request = new TicketRequest();
        request.setTitle("Тест");
        request.setDescription("Описание");
        request.setCategoryId(softwareCategory.getCategoryId());
        request.setPriorityId(lowPriority.getPriorityId());

        mockMvc.perform(post("/api/tickets")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/tickets - пользователь видит только свои заявки")
    public void testGetAllTickets_UserSeesOnlyOwnTickets() throws Exception {
        // Создаем заявку от обычного пользователя
        Ticket userTicket = createTicket("Заявка пользователя", regularUser);

        // Создаем заявку от оператора
        Ticket operatorTicket = createTicket("Заявка оператора", operatorUser);

        String token = getUserToken();

        mockMvc.perform(get("/api/tickets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Заявка пользователя"))
                .andExpect(jsonPath("$[0].createdBy.login").value("user"));
    }

    @Test
    @DisplayName("GET /api/tickets - оператор видит все заявки")
    public void testGetAllTickets_OperatorSeesAll() throws Exception {
        // Создаем несколько заявок
        createTicket("Заявка 1", regularUser);
        createTicket("Заявка 2", operatorUser);

        String token = getOperatorToken();

        mockMvc.perform(get("/api/tickets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/tickets/{id} - получение заявки по ID")
    public void testGetTicketById_Success() throws Exception {
        Ticket ticket = createTicket("Тестовая заявка", regularUser);
        String token = getUserToken();

        mockMvc.perform(get("/api/tickets/" + ticket.getTicketId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(ticket.getTicketId()))
                .andExpect(jsonPath("$.title").value("Тестовая заявка"))
                .andExpect(jsonPath("$.ticketNumber").exists());
    }

    @Test
    @DisplayName("GET /api/tickets/{id} - пользователь не может видеть чужую заявку")
    public void testGetTicketById_Forbidden() throws Exception {
        Ticket ticket = createTicket("Заявка оператора", operatorUser);
        String token = getUserToken();

        mockMvc.perform(get("/api/tickets/" + ticket.getTicketId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/tickets/{id}/status - изменение статуса оператором")
    public void testUpdateTicketStatus_Success() throws Exception {
        Ticket ticket = createTicket("Заявка", regularUser);
        String token = getOperatorToken();

        Map<String, Long> request = new HashMap<>();
        request.put("statusId", inProgressStatus.getStatusId());

        mockMvc.perform(put("/api/tickets/" + ticket.getTicketId() + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.name").value("В работе"));
    }

    @Test
    @DisplayName("PUT /api/tickets/{id}/status - пользователь не может менять статус")
    public void testUpdateTicketStatus_Forbidden() throws Exception {
        Ticket ticket = createTicket("Заявка", regularUser);
        String token = getUserToken();

        Map<String, Long> request = new HashMap<>();
        request.put("statusId", inProgressStatus.getStatusId());

        mockMvc.perform(put("/api/tickets/" + ticket.getTicketId() + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/tickets/{id}/assign - назначение оператора")
    public void testAssignTicket_Success() throws Exception {
        Ticket ticket = createTicket("Заявка", regularUser);
        String token = getOperatorToken();

        Map<String, Long> request = new HashMap<>();
        request.put("operatorId", operatorUser.getUserId());

        mockMvc.perform(put("/api/tickets/" + ticket.getTicketId() + "/assign")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedTo.login").value("operator"))
                .andExpect(jsonPath("$.status.name").value("В работе")); // Автоматически меняется статус
    }

    @Test
    @DisplayName("GET /api/tickets/my - получение заявок назначенных оператору")
    public void testGetMyTickets_Success() throws Exception {
        // Создаем заявку и назначаем оператору
        Ticket ticket1 = createTicket("Заявка 1", regularUser);
        ticket1.setAssignedTo(operatorUser);
        ticketRepository.save(ticket1);

        // Создаем заявку без назначения
        createTicket("Заявка 2", regularUser);

        String token = getOperatorToken();

        mockMvc.perform(get("/api/tickets/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Заявка 1"))
                .andExpect(jsonPath("$[0].assignedTo.login").value("operator"));
    }

    @Test
    @DisplayName("GET /api/tickets/{id}/history - получение истории заявки")
    public void testGetTicketHistory_Success() throws Exception {
        Ticket ticket = createTicket("Заявка", regularUser);

        // Назначаем оператора (это создаст историю)
        ticket.setAssignedTo(operatorUser);
        ticket.setStatus(inProgressStatus);
        ticketRepository.save(ticket);

        String token = getUserToken();

        mockMvc.perform(get("/api/tickets/" + ticket.getTicketId() + "/history")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("GET /api/tickets/999 - заявка не найдена")
    public void testGetTicketById_NotFound() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/tickets/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()); // 404 - заявка не найдена
    }

    // Helper method для создания тестовой заявки
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

        // Генерируем номер заявки
        ticket.setTicketNumber("TKT-2026-" + String.format("%03d", ticket.getTicketId()));
        return ticketRepository.save(ticket);
    }
}
