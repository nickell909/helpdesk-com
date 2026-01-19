package com.helpdesk.repository;

import com.helpdesk.entity.Ticket;
import com.helpdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    List<Ticket> findByCreatedBy(User user);
    List<Ticket> findByAssignedTo(User user);

    @Query("SELECT MAX(t.ticketId) FROM Ticket t")
    Long findMaxTicketId();
}
