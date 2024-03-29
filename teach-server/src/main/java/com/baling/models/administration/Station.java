package com.baling.models.administration;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "station")
public class Station {
    @Id
    @Size(max = 20)
    private String id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotNull
    private double lon;

    @NotNull
    private double lat;

    @NotNull
    private double timeZone;

    @ManyToOne
    @NotNull
    @JoinColumn(name="city_id")
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

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(double timeZone) {
        this.timeZone = timeZone;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

}
