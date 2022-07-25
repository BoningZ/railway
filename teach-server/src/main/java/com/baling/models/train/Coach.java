package com.baling.models.train;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "coach")
public class Coach {
    @Id
    @Size(max = 20)
    private String id;

    @NotBlank
    @Size(max = 20)
    private String name;

    @NotBlank
    private int maxFreeCount;

    private boolean isRefrigerated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMaxFreeCount() {
        return maxFreeCount;
    }

    public void setMaxFreeCount(int maxFreeCount) {
        this.maxFreeCount = maxFreeCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRefrigerated() {
        return isRefrigerated;
    }

    public void setRefrigerated(boolean refrigerated) {
        isRefrigerated = refrigerated;
    }
}
