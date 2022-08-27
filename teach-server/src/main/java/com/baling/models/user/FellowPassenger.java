package com.baling.models.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "fellow_passenger",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"host_id","fellow_id"})
})
public class FellowPassenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "host_id")
    private Passenger host;

    @ManyToOne
    @NotNull
    @JoinColumn(name="fellow_id")
    private Passenger fellow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Passenger getHost() {
        return host;
    }

    public void setHost(Passenger host) {
        this.host = host;
    }

    public Passenger getFellow() {
        return fellow;
    }

    public void setFellow(Passenger fellow) {
        this.fellow = fellow;
    }
}
