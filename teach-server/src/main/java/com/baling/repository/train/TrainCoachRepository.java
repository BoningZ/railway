package com.baling.repository.train;

import com.baling.models.train.Train;
import com.baling.models.train.TrainCoach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainCoachRepository extends JpaRepository<TrainCoach, Integer> {
    List<TrainCoach> findByTrain(Train train);
}
