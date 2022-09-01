package com.baling.repository.ticket;

import com.baling.models.ticket.Travel;
import com.baling.models.user.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelRepository extends JpaRepository<Travel,String> {
    List<Travel> findByPassenger(Passenger passenger);
}
