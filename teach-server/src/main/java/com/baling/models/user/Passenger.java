package com.baling.models.user;

import com.baling.models.administration.Country;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name="passenger",
uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class Passenger {
    @Id
    @Size(max = 20)
    private String id;

    @Size(max = 20)
    private String tel;

    @NotBlank
    @Size(max=60)
    private String name;

    private java.sql.Date birthday;

    @ManyToOne
    @NotNull
    @JoinColumn(name="country_id")
    private Country country;

    @OneToOne
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
