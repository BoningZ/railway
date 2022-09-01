package com.baling.repository.ticket;

import com.baling.models.line.Departure;
import com.baling.models.ticket.Ticket;
import com.baling.models.ticket.TicketStatus;
import com.baling.models.ticket.Travel;
import com.baling.models.train.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    List<Ticket> findByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(Departure departure, Date startDate, TicketStatus ticketStatus,Seat seat,int end,int start);
    int countByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(Departure departure, Date startDate, TicketStatus ticketStatus, Seat seat,int end,int start);
    List<Ticket> findByDepartureAndStartDateAndTicketStatusAndSeatAndColAndCoachNumAndStartLessThanAndEndGreaterThanOrderByRowPosition(Departure departure, Date startDate,TicketStatus ticketStatus,Seat seat,String col,int coachNum,int end,int start);
    List<Ticket> findByTravelOrderByOrderInTravel(Travel travel);
}
