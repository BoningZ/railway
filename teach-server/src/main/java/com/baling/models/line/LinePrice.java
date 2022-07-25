package com.baling.models.line;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name = "line_price")
public class LinePrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @NotBlank
    private java.sql.Date updated;

    @NotBlank
    private int inDay;

    @NotBlank
    private int inWeek;

    @NotBlank
    private boolean isHoliday;

    @NotBlank
    private double power;

    @NotBlank
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getInDay() {
        return inDay;
    }

    public void setInDay(int inDay) {
        this.inDay = inDay;
    }

    public int getInWeek() {
        return inWeek;
    }

    public void setInWeek(int inWeek) {
        this.inWeek = inWeek;
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    public void setHoliday(boolean holiday) {
        isHoliday = holiday;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }
}
