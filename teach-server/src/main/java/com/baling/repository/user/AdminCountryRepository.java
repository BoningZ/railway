package com.baling.repository.user;

import com.baling.models.user.AdminCountry;
import com.baling.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminCountryRepository extends JpaRepository<AdminCountry,String> {
    boolean existsById(String id);
    AdminCountry getByUser(User user);
}
