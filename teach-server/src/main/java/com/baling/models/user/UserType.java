package com.baling.models.user;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "user_type",
uniqueConstraints ={ @UniqueConstraint(columnNames = "name")}
)
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NotBlank
    private EUserType name;

    public UserType() {

    }

    public UserType(EUserType name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EUserType getName() {
        return name;
    }

    public void setName(EUserType name) {
        this.name = name;
    }
}
