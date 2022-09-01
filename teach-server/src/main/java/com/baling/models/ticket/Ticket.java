package com.baling.models.ticket;

import com.baling.models.line.Departure;
import com.baling.models.train.Seat;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name = "ticket")
public class Ticket implements Cloneable{
    @Id
    @Size(max = 20)
    private String id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "ticket_status_id")
    private TicketStatus ticketStatus;

    @NotNull
    private java.sql.Date startDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "departure_id")
    private Departure departure;

    @NotNull
    private int start;

    @NotNull
    private int end;

    @NotNull
    private double price;

    @NotNull
    private int coachNum;

    private int rowPosition;

    @Size(max = 10)
    private String col;

    @NotNull
    private int altered;

    @NotNull
    private int orderInTravel;

    public Ticket clone(){
        try {
            return (Ticket) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

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
