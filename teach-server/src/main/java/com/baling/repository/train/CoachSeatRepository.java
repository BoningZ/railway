package com.baling.repository.train;

import com.baling.models.train.Coach;
import com.baling.models.train.CoachSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachSeatRepository extends JpaRepository<CoachSeat, Integer> {
    List<CoachSeat> findByCoach(Coach coach);
}
