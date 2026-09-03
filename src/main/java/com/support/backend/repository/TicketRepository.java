package com.support.backend.repository;

import com.support.backend.entity.Ticket;
import com.support.backend.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketCodeIgnoreCase(String ticketCode);

    // Used later by the scheduler (Phase 8) to find overdue tickets to follow up on
    List<Ticket> findByStatusInAndCreatedAtBefore(List<TicketStatus> statuses, Instant cutoff);
}
