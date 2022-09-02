package com.baling.repository.train;

import com.baling.models.train.CoachSeat;
import com.baling.models.train.Train;
import com.baling.models.train.TrainCoach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainCoachRepository extends JpaRepository<TrainCoach, Integer> {
    List<TrainCoach> findByTrain(Train train);
    @Query(value = "select * from train_coach where train_id=?1 and (position&?2)>0 limit 1",nativeQuery = true)
    TrainCoach nativeFindByTrainIdAndPositionBit(String trainId, int positionBit);
}
