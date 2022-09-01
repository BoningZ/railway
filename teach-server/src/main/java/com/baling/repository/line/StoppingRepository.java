package com.baling.repository.line;

import com.baling.models.administration.Station;
import com.baling.models.line.Line;
import com.baling.models.line.Stopping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.validation.Valid;
import java.util.List;

public interface StoppingRepository extends JpaRepository<Stopping,Integer> {
    List<Stopping> findByStation(Station station);
    List<Stopping> findByLineOrderByOrderInLine(Line line);
    Stopping findByLineAndOrderInLine(Line line,int orderInLine);
    Stopping findByLineAndStation(Line line,Station station);
    @Query(value = "select * from stopping where line_id=?1 and station_id=?2 order by order_in_line desc limit 1",nativeQuery = true)
    Stopping findLastByLineAndStation(String lineId,String stationId);
    @Query(value = "select * from stopping where line_id=?1 and station_id=?2 order by order_in_line asc limit 1",nativeQuery = true)
    Stopping findFirstByLineAndStation(String lineId,String stationId);
    @Query(value = "select * from stopping where line_id=?1 ORDER BY order_in_line asc limit 1",nativeQuery = true)
    Stopping findFirstInLine(String lineId);
    @Query(value = "select * from stopping where line_id=?1 and station_id in (select id from station where city_id=?2) order by order_in_line desc limit 1",nativeQuery = true)
    Stopping findLastByLineAndCity(String lineId,String cityId);
    @Query(value = "select * from stopping where line_id=?1 and station_id in (select id from station where city_id=?2) order by order_in_line asc limit 1",nativeQuery = true)
    Stopping findFirstByLineAndCity(String lineId,String cityId);

    @Query(value = "select * from stopping where line_id=?1 and order_in_line>=?2 and order_in_line<=?3 order by order_in_line",nativeQuery = true)
    List<Stopping> findBetween(String lineId,int o1,int o2);

}
