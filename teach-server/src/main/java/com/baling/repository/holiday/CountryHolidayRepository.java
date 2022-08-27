package com.baling.repository.holiday;

import com.baling.models.administration.Country;
import com.baling.models.holiday.CountryHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryHolidayRepository extends JpaRepository<CountryHoliday,Integer> {
    List<CountryHoliday> findByCountry(Country country);
}
