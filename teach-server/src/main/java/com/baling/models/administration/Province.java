package com.baling.models.administration;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="province")
public class Province {
    @Id
    @Size(max = 20)
    private String id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "country_id")
    private Country country;

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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
