package com.baling.repository.line;

import com.baling.models.line.Departure;
import com.baling.models.line.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartureRepository extends JpaRepository<Departure,String> {
    List<Departure> findByLine(Line line);

}
