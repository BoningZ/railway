package com.baling.models.administration;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="company_refund")
public class CompanyRefund {
    @Id
    @Size(max = 20)
    private String id;

    @ManyToOne
    @NotBlank
    @JoinColumn(name = "company_id")
    private Company company;

    @NotBlank
    private double leftHour;

    @NotBlank
    private double ratio;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public double getLeftHour() {
        return leftHour;
    }

    public void setLeftHour(double leftHour) {
        this.leftHour = leftHour;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
