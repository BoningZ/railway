package com.baling.repository.train;

import com.baling.models.train.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train,String> {
    List<Train> findByIdLike(String id);
    List<Train> findByNameLike(String name);
}
