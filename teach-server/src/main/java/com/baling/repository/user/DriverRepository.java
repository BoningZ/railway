package com.baling.repository.user;

import com.baling.models.user.Driver;
import com.baling.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver,String> {
    boolean existsById(String id);
    Driver findByUser(User user);
}
