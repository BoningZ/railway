package com.baling.models.ticket;

import com.baling.models.line.Departure;
import com.baling.models.train.Seat;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @Size(max = 20)
    private String id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "ticket_status_id")
    private TicketStatus ticketStatus;

    @NotBlank
    private java.sql.Date startDate;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "departure_id")
    private Departure departure;

    @NotBlank
    private int start;

    @NotBlank
    private int end;

    @NotBlank
    private double price;

    @NotBlank
    private int coachNum;

    private int rowPosition;

    @Size(max = 10)
    private String col;

    @NotBlank
    private int altered;

    @NotBlank
    private int orderInTravel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCoachNum() {
        return coachNum;
    }

    public void setCoachNum(int coachNum) {
        this.coachNum = coachNum;
    }

    public int getRowPosition() {
        return rowPosition;
    }

    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public int getAltered() {
        return altered;
    }

    public void setAltered(int altered) {
        this.altered = altered;
    }

    public int getOrderInTravel() {
        return orderInTravel;
    }

    public void setOrderInTravel(int orderInTravel) {
        this.orderInTravel = orderInTravel;
    }
}
