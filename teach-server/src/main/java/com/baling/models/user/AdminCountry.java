package com.baling.models.user;

import com.baling.models.administration.Country;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="admin_country")
public class AdminCountry {
    @Id
    @Size(max = 20)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotBlank
    private User user;

    @ManyToOne
    @JoinColumn(name="country_id")
    @NotBlank
    private Country country;

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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
