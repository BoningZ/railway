package com.baling.repository.line;

import com.baling.models.administration.Station;
import com.baling.models.line.Line;
import com.baling.models.line.Stopping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoppingRepository extends JpaRepository<Stopping,Integer> {
    List<Stopping> findByStation(Station station);
    List<Stopping> findByLineOrderByOrderInLine(Line line);
    Stopping findByLineAndOrderInLine(Line line,int orderInLine);
    Stopping findByLineAndStation(Line line,Station station);

}
