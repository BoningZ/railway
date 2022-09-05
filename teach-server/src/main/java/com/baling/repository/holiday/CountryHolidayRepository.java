package com.baling.repository.holiday;

import com.baling.models.administration.Country;
import com.baling.models.holiday.CountryHoliday;
import com.baling.models.holiday.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryHolidayRepository extends JpaRepository<CountryHoliday,Integer> {
    List<CountryHoliday> findByCountry(Country country);
    Boolean existsByCountryAndHoliday(Country country, Holiday holiday);
}
