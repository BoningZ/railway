package com.baling.repository.user;

import com.baling.models.user.FellowPassenger;
import com.baling.models.user.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FellowPassengerRepository extends JpaRepository<FellowPassenger,Integer> {
    List<FellowPassenger> getByHost(Passenger passenger);
    FellowPassenger getByHostAndFellow(Passenger host,Passenger fellow);
}
