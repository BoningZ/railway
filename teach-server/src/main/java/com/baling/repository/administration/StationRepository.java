package com.baling.repository.administration;

import com.baling.models.administration.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station,String> {
    List<Station> getAllBy();
    List<Station> getStationsByNameLike(String name);
}
