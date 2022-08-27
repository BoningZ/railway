package com.baling.repository.ticket;

import com.baling.models.ticket.ETicketStatus;
import com.baling.models.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;

public interface TicketStatusRepository extends JpaRepository<TicketStatus,Integer> {
    TicketStatus getByName(@NotNull ETicketStatus name);
}
