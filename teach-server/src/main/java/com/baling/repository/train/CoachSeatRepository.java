package com.baling.repository.train;

import com.baling.models.train.Coach;
import com.baling.models.train.CoachSeat;
import com.baling.models.train.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CoachSeatRepository extends JpaRepository<CoachSeat, Integer> {
    List<CoachSeat> findByCoach(Coach coach);

    List<CoachSeat> findByCoachAndSeatAndAndColsContains(Coach coach, Seat seat,String cols);
}
