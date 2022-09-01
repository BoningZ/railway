package com.baling.models.train;

import com.baling.util.CommonMethod;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "coach_seat")
public class CoachSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "coach_id")
    @Size(max = 20)
    private Coach coach;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "seat_id")
    @Size(max = 20)
    private Seat seat;



    private int rowsPosition;

    @Size(max = 20)
    private String cols;

    public int numOfSeats(){
        if(rowsPosition==-1)return coach.getMaxFreeCount();
        if(cols.equals("顺序编号"))return rowsPosition;
        return CommonMethod.numOfOnes(rowsPosition)* cols.split(",|/").length;
    }

    public List<List<String>> getColList(){
        if(cols==null)return Collections.singletonList(Collections.singletonList("自由席"));
        List<List<String>> res=new ArrayList<>();
        for(String split:cols.split("/"))
            res.add(Arrays.asList(split.split(",")));
        return res;
    }

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
