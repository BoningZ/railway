package com.baling.repository.user;

import com.baling.models.user.AdminStation;
import com.baling.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminStationRepository extends JpaRepository<AdminStation,String> {
    boolean existsById(String id);
    AdminStation getByUser(User user);
}
