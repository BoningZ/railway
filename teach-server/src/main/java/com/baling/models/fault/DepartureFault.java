package com.baling.models.fault;

import com.baling.models.line.Departure;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "departure_fault")
public class DepartureFault {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "departure_id")
    private Departure departure;

    @NotBlank
    private java.sql.Date startDate;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "fault_id")
    private Fault fault;

    private java.sql.Time delay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Fault getFault() {
        return fault;
    }

    public void setFault(Fault fault) {
        this.fault = fault;
    }

    public Time getDelay() {
        return delay;
    }

    public void setDelay(Time delay) {
        this.delay = delay;
    }
}
