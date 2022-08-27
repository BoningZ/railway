package com.baling.repository.holiday;

import com.baling.models.holiday.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday,String> {
}
