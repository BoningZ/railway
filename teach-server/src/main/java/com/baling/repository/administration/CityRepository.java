package com.baling.repository.administration;

import com.baling.models.administration.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City,String> {
    List<City> findByNameLike(String s);

}
