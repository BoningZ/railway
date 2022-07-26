package com.baling.models.administration;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="currency",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Currency {
    @Id
    @Size(max=20)
    private String id;

    @Size(max=20)
    @NotBlank
    private String name;

    @JoinColumn(name = "to_rmb")
    @NotBlank
    private double toRmb;

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

    public double getToRmb() {
        return toRmb;
    }

    public void setToRmb(double toRmb) {
        this.toRmb = toRmb;
    }
}
