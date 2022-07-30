package com.baling.models.line;

import com.baling.models.user.Driver;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Time;

@Entity
@Table(name = "departure")
public class Departure {
    @Id
    @Size(max = 20)
    private String id;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "line_id")
    private Line line;

    @NotBlank
    private int start;

    @NotBlank
    private int schedule;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSchedule() {
        return schedule;
    }

    public void setSchedule(int schedule) {
        this.schedule = schedule;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
