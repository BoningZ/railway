package com.baling.models.user;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="admin_global",
uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class AdminGlobal {
    @Id
    @Size(max = 20)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
