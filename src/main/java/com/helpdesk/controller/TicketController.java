package com.helpdesk.controller;

import com.helpdesk.dto.TicketRequest;
import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.TicketHistory;
import com.helpdesk.entity.User;
import com.helpdesk.service.TicketHistoryService;
import com.helpdesk.service.TicketService;
import com.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @Autowired
    private TicketHistoryService ticketHistoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<List<Ticket>> getAllTickets(Authentication authentication) {
        User user = userService.getUserByLogin(authentication.getName());

        // Если пользователь - USER, показываем только его заявки
        if (user.getRole().getName().equals("USER")) {
            return ResponseEntity.ok(ticketService.getTicketsByUser(user));
        }

        // Для OPERATOR и ADMIN показываем все заявки
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id, Authentication authentication) {
        Ticket ticket = ticketService.getTicketById(id);
        User user = userService.getUserByLogin(authentication.getName());

        // Проверяем права доступа
        if (user.getRole().getName().equals("USER") &&
                !ticket.getCreatedBy().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketRequest request,
                                                Authentication authentication) {
        Ticket ticket = ticketService.createTicket(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public ResponseEntity<Ticket> updateTicketStatus(@PathVariable Long id,
                                                      @RequestBody Map<String, Long> request,
                                                      Authentication authentication) {
        Long statusId = request.get("statusId");
        Ticket ticket = ticketService.updateTicketStatus(id, statusId, authentication.getName());
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public ResponseEntity<Ticket> assignTicket(@PathVariable Long id,
                                                @RequestBody Map<String, Long> request,
                                                Authentication authentication) {
        Long operatorId = request.get("operatorId");
        Ticket ticket = ticketService.assignTicket(id, operatorId, authentication.getName());
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('OPERATOR', 'ADMIN')")
    public ResponseEntity<List<Ticket>> getMyTickets(Authentication authentication) {
        User operator = userService.getUserByLogin(authentication.getName());
        return ResponseEntity.ok(ticketService.getTicketsByOperator(operator));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('USER', 'OPERATOR', 'ADMIN')")
    public ResponseEntity<List<TicketHistory>> getTicketHistory(@PathVariable Long id, Authentication authentication) {
        Ticket ticket = ticketService.getTicketById(id);
        User user = userService.getUserByLogin(authentication.getName());

        // Проверяем права доступа
        if (user.getRole().getName().equals("USER") &&
                !ticket.getCreatedBy().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TicketHistory> history = ticketHistoryService.getTicketHistory(ticket);
        return ResponseEntity.ok(history);
    }
}
