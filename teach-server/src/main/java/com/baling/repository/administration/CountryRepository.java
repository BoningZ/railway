package com.baling.repository.administration;

import com.baling.models.administration.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country,String> {
    List<Country> getAllBy();
}
