package com.baling.repository.ticket;

import com.baling.models.line.Departure;
import com.baling.models.ticket.Ticket;
import com.baling.models.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    List<Ticket> findByDepartureAndStartDateAndTicketStatus(Departure departure, Date startDate, TicketStatus ticketStatus);
    int countByDepartureAndStartDateAndTicketStatus(Departure departure, Date startDate, TicketStatus ticketStatus);
}
