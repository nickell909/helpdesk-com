package com.helpdesk.service;

import com.helpdesk.dto.TicketRequest;
import com.helpdesk.entity.*;
import com.helpdesk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketHistoryService ticketHistoryService;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена: " + id));
    }

    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepository.findByCreatedBy(user);
    }

    public List<Ticket> getTicketsByOperator(User operator) {
        return ticketRepository.findByAssignedTo(operator);
    }

    public Ticket createTicket(TicketRequest request, String username) {
        User creator = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категория не найдена: " + request.getCategoryId()));

        Priority priority = priorityRepository.findById(request.getPriorityId())
                .orElseThrow(() -> new RuntimeException("Приоритет не найден: " + request.getPriorityId()));

        Status newStatus = statusRepository.findByName("Новая")
                .orElseThrow(() -> new RuntimeException("Статус 'Новая' не найден"));

        // Генерируем номер заявки ДО сохранения
        String ticketNumber = generateTicketNumber();

        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setStatus(newStatus);
        ticket.setCreatedBy(creator);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public Ticket updateTicketStatus(Long ticketId, Long statusId, String username) {
        Ticket ticket = getTicketById(ticketId);
        Status oldStatus = ticket.getStatus();

        Status newStatus = statusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Статус не найден: " + statusId));

        User changedBy = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());

        // Записываем изменение в историю
        ticketHistoryService.recordChange(ticket, changedBy, "status",
                oldStatus.getName(), newStatus.getName());

        return ticketRepository.save(ticket);
    }

    public Ticket assignTicket(Long ticketId, Long operatorId, String username) {
        Ticket ticket = getTicketById(ticketId);
        User oldOperator = ticket.getAssignedTo();

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("Оператор не найден: " + operatorId));

        User changedBy = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        ticket.setAssignedTo(operator);
        ticket.setUpdatedAt(LocalDateTime.now());

        // Записываем изменение в историю
        String oldValue = oldOperator != null ? oldOperator.getFullName() : "Не назначен";
        ticketHistoryService.recordChange(ticket, changedBy, "assigned_to",
                oldValue, operator.getFullName());

        // Автоматически переводим в статус "В работе"
        Status inProgressStatus = statusRepository.findByName("В работе")
                .orElseThrow(() -> new RuntimeException("Статус 'В работе' не найден"));

        if (!ticket.getStatus().equals(inProgressStatus)) {
            Status oldStatus = ticket.getStatus();
            ticket.setStatus(inProgressStatus);
            ticketHistoryService.recordChange(ticket, changedBy, "status",
                    oldStatus.getName(), inProgressStatus.getName());
        }

        return ticketRepository.save(ticket);
    }

    private String generateTicketNumber() {
        int year = Year.now().getValue();
        String prefix = String.format("TKT-%d-", year);

        // Находим максимальный номер за текущий год
        List<Ticket> ticketsThisYear = ticketRepository.findAll().stream()
                .filter(t -> t.getTicketNumber() != null && t.getTicketNumber().startsWith(prefix))
                .toList();

        int maxNumber = ticketsThisYear.stream()
                .map(t -> {
                    try {
                        String numPart = t.getTicketNumber().substring(prefix.length());
                        return Integer.parseInt(numPart);
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Integer::compareTo)
                .orElse(0);

        return String.format("TKT-%d-%04d", year, maxNumber + 1);
    }
}
