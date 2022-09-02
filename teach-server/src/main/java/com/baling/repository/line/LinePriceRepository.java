package com.baling.repository.line;

import com.baling.models.line.LinePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LinePriceRepository extends JpaRepository<LinePrice, Integer> {
    @Query(value = "select * from line_price where line_id=?1 and (in_day&?2)>0 and (in_week&?3)>0 and is_holiday=?4 order by updated",nativeQuery = true)
    List<LinePrice> getByLineAndDayAndWeekAndHoliday(String lineId,int inDay,int inWeek,boolean isHoliday);
}
