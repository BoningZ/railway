package com.baling.repository.administration;

import com.baling.models.administration.City;
import com.baling.models.administration.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StationRepository extends JpaRepository<Station,String> {
    List<Station> getAllBy();
    List<Station> getStationsByNameLike(String name);
    List<Station> findByCity(City city);
    @Query(value = "SELECT * FROM station where id in (select station_id from stopping where line_id in (select line_id from stopping where station_id=?1))",nativeQuery = true)
    List<Station> findDirectStations(String id);
}
