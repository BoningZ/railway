package com.baling.models.fault;

import com.baling.models.line.Line;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "line_fault")
public class LineFault {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "fault_id")
    private Fault fault;

    private int orderInLine;

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

    public Fault getFault() {
        return fault;
    }

    public void setFault(Fault fault) {
        this.fault = fault;
    }

    public int getOrderInLine() {
        return orderInLine;
    }

    public void setOrderInLine(int orderInLine) {
        this.orderInLine = orderInLine;
    }
}
