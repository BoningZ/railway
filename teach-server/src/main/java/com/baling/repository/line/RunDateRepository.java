package com.baling.repository.line;

import com.baling.models.line.Line;
import com.baling.models.line.RunDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunDateRepository extends JpaRepository<RunDate,Integer> {
    List<RunDate> findByLine(Line line);
}
