package com.baling.repository.train;

import com.baling.models.train.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachRepository extends JpaRepository<Coach,String> {
    List<Coach> findByIdLike(String id);
}
