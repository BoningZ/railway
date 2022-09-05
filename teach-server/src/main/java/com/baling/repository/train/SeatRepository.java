package com.baling.repository.train;

import com.baling.models.train.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,String> {
    @Query(value = "select * from seat where id in (select seat_id from coach_seat where coach_id in (select coach_id from train_coach where train_id=?))",nativeQuery = true)
    List<Seat> findByTrain(String trainId);
}
