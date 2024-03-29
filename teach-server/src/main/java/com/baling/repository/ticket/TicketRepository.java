package com.baling.repository.ticket;

import com.baling.models.line.Departure;
import com.baling.models.line.Line;
import com.baling.models.ticket.Ticket;
import com.baling.models.ticket.TicketStatus;
import com.baling.models.ticket.Travel;
import com.baling.models.train.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    List<Ticket> findByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(Departure departure, Date startDate, TicketStatus ticketStatus,Seat seat,int end,int start);
    int countByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(Departure departure, Date startDate, TicketStatus ticketStatus, Seat seat,int end,int start);
    List<Ticket> findByDepartureAndStartDateAndTicketStatusAndSeatAndColAndCoachNumAndStartLessThanAndEndGreaterThanOrderByRowPosition(Departure departure, Date startDate,TicketStatus ticketStatus,Seat seat,String col,int coachNum,int end,int start);
    List<Ticket> findByDepartureAndStartDateAndTicketStatusAndSeat(Departure departure,Date startDate,TicketStatus ticketStatus,Seat seat);
    List<Ticket> findByTravelOrderByOrderInTravel(Travel travel);

    @Query(value = "select * from ticket where departure_id in (select id from departure where line_id=?1) and ticket_status_id=?2",nativeQuery = true)
    List<Ticket> findByLineAndTicketStatus(String lineId,int ticketStatusId);
    List<Ticket> findByDepartureAndTicketStatusAndStartDateBetween(Departure departure,TicketStatus ticketStatus,Date date1,Date date2);
}
