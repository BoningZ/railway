package com.baling.models.ticket;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ticket_status",
uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class TicketStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NotBlank
    private ETicketStatus name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ETicketStatus getName() {
        return name;
    }

    public void setName(ETicketStatus name) {
        this.name = name;
    }
}
