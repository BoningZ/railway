package com.baling.repository.user;

import com.baling.models.user.Passenger;
import com.baling.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger,String> {
    boolean existsById(String id);
    Passenger findByUser(User user);
}
