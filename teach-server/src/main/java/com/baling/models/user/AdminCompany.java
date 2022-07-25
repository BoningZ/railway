package com.baling.models.user;

import com.baling.models.administration.Company;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="admin_company")
public class AdminCompany {
    @Id
    @Size(max = 20)
    private String id;

    @OneToOne
    @JoinColumn(name="user_id")
    @NotBlank
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @NotBlank
    private Company company;

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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
