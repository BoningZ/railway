package com.baling.repository.train;

import com.baling.models.train.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat,String> {
}
