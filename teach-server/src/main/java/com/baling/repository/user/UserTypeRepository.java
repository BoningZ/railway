package com.baling.repository.user;

import com.baling.models.user.EUserType;
import com.baling.models.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Integer> {
    UserType findByName(EUserType name);
}
