package com.baling.models.line;

import com.baling.models.administration.Station;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;

@Entity
@Table(name = "stopping")
public class Stopping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "station_id")
    private Station station;

    @NotNull
    private int orderInLine;

    @NotNull
    private int arrival;

    @NotNull
    private int stay;

    @NotNull
    private double constant;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getOrderInLine() {
        return orderInLine;
    }

    public void setOrderInLine(int orderInLine) {
        this.orderInLine = orderInLine;
    }

    public int getArrival() {
        return arrival;
    }

    public void setArrival(int arrival) {
        this.arrival = arrival;
    }

    public int getStay() {
        return stay;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }
}
