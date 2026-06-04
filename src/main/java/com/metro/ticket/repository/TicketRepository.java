package com.metro.ticket.repository;
import java.util.List;
import com.metro.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
List<Ticket> findByMobileNumber(String mobileNumber);
    Optional<Ticket> findByQrToken(String qrToken);

    // ✅ NEW: For ticket number generation
    long countByIssuedAtBetween(LocalDateTime start, LocalDateTime end);
}