package com.baling.models.administration;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "company")
public class Company {
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

    @Size(max = 60)
    private String website;

    @NotBlank
    private int maxAlter;

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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getMaxAlter() {
        return maxAlter;
    }

    public void setMaxAlter(int maxAlter) {
        this.maxAlter = maxAlter;
    }
}
