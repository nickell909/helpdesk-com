package com.helpdesk.service;

import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.TicketHistory;
import com.helpdesk.entity.User;
import com.helpdesk.repository.TicketHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TicketHistoryService {

    @Autowired
    private TicketHistoryRepository ticketHistoryRepository;

    public List<TicketHistory> getHistoryByTicket(Ticket ticket) {
        return ticketHistoryRepository.findByTicketOrderByChangedAtDesc(ticket);
    }

    public List<TicketHistory> getTicketHistory(Ticket ticket) {
        return getHistoryByTicket(ticket);
    }

    public void recordChange(Ticket ticket, User changedBy, String fieldName, String oldValue, String newValue) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setChangedBy(changedBy);
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedAt(LocalDateTime.now());

        ticketHistoryRepository.save(history);
    }
}
