package com.baling.repository.user;

import com.baling.models.user.AdminCompany;
import com.baling.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminCompanyRepository extends JpaRepository<AdminCompany,String> {
    boolean existsById(String id);
    AdminCompany getByUser(User user);
}
