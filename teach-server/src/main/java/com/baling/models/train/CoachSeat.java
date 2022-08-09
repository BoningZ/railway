package com.baling.models.train;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "coach_seat")
public class CoachSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "coach_id")
    @Size(max = 20)
    private Coach coach;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "seat_id")
    @Size(max = 20)
    private Seat seat;



    private int rowsPosition;

    @Size(max = 20)
    private String cols;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public int getRowsPosition() {
        return rowsPosition;
    }

    public void setRowsPosition(int rowsPosition) {
        this.rowsPosition = rowsPosition;
    }

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }
}
