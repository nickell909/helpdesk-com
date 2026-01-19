package com.helpdesk.controller;

import com.helpdesk.BaseIntegrationTest;
import com.helpdesk.entity.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Report API Tests")
public class ReportControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("GET /api/reports/pdf - генерация PDF отчета")
    public void testGeneratePdfReport_Success() throws Exception {
        // Создаем несколько заявок для отчета
        createTicket("Заявка 1", regularUser);
        createTicket("Заявка 2", regularUser);

        String token = getOperatorToken();

        mockMvc.perform(get("/api/reports/pdf")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("helpdesk-report-")));
    }

    @Test
    @DisplayName("GET /api/reports/pdf - доступ запрещен для обычного пользователя")
    public void testGeneratePdfReport_ForbiddenForUser() throws Exception {
        String token = getUserToken();

        mockMvc.perform(get("/api/reports/pdf")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/reports/pdf - админ может генерировать отчет")
    public void testGeneratePdfReport_Admin() throws Exception {
        String token = getAdminToken();

        mockMvc.perform(get("/api/reports/pdf")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    @DisplayName("GET /api/reports/pdf - без авторизации")
    public void testGeneratePdfReport_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/reports/pdf"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/reports/pdf - генерация отчета с пустой БД")
    public void testGeneratePdfReport_EmptyDatabase() throws Exception {
        // Очищаем все заявки
        ticketRepository.deleteAll();

        String token = getOperatorToken();

        mockMvc.perform(get("/api/reports/pdf")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
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
