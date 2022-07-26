package com.baling.models.administration;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "district")
public class District {
    @Id
    @Size(max = 20)
    private String id;

    @Size(max = 20)
    @NotBlank
    private String name;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "city_id")
    private City city;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
