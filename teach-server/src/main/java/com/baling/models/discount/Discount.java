package com.baling.models.discount;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "discount")
public class Discount {
    @Id
    @Size(max = 20)
    private String id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    private double power;

    private int limitInYear;

    @NotBlank
    private boolean isRegional;

    @NotBlank
    private boolean isOpen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public int getLimitInYear() {
        return limitInYear;
    }

    public void setLimitInYear(int limitInYear) {
        this.limitInYear = limitInYear;
    }

    public boolean isRegional() {
        return isRegional;
    }

    public void setRegional(boolean regional) {
        isRegional = regional;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
