package com.baling.repository.line;

import com.baling.models.line.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineRepository extends JpaRepository<Line,String> {
    @Query(value = "select * from line where id in (select line_id from stopping stp where station_id=?1 and order_in_line<some(select order_in_line from stopping where line_id=stp.line_id and station_id=?2) group by line_id)",nativeQuery = true)
    List<Line> findByOrderedStations(String fromSta,String toSta);//all lines from a to b

    @Query(value = "select * from line where id in (select line_id from stopping stp where station_id=?1 and order_in_line<some(select order_in_line from stopping where line_id=stp.line_id and station_id=?2))",nativeQuery = true)
    List<Line> findFromStationToStation(String from,String to);
    @Query(value = "select * from line where id in (select line_id from stopping stp where station_id=?1 and order_in_line<some(select order_in_line from stopping where line_id=stp.line_id and station_id in (select id from station where city_id=?2)))",nativeQuery = true)
    List<Line> findFromStationToCity(String from,String to);
    @Query(value = "select * from line where id in (select line_id from stopping stp where station_id in (select id from station where city_id=?1) and order_in_line<some(select order_in_line from stopping where line_id=stp.line_id and station_id in (select id from station where city_id=?2)))",nativeQuery = true)
    List<Line> findFromCityToCity(String from,String to);
    @Query(value = "select * from line where id in (select line_id from stopping stp where station_id in (select id from station where city_id=?1) and order_in_line<some(select order_in_line from stopping where line_id=stp.line_id and station_id =?2))",nativeQuery = true)
    List<Line> findFromCityToStation(String from,String to);

    @Query(value = "select * from line where (id like ?1 or name like ?1) and company_id=?2 limit 30",nativeQuery = true)
    List<Line> findTopHundredByFeatureLikeAndCompany(String feature,String companyId);


}
