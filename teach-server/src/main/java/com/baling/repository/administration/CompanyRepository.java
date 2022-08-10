package com.baling.repository.administration;

import com.baling.models.administration.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company,String> {
    List<Company> getAllBy();
}
