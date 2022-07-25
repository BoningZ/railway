package com.baling.models.ticket;

import com.baling.models.user.Passenger;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "travel")
public class Travel {
    @Id
    @Size(max = 20)
    private String id;

    @NotBlank
    private java.util.Date buyTime;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
}
