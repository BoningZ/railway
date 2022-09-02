package com.baling.repository.administration;

import com.baling.models.administration.Company;
import com.baling.models.administration.CompanyRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

public interface CompanyRefundRepository extends JpaRepository<CompanyRefund, Integer> {
    List<CompanyRefund> findByCompanyOrderByLeftHour(Company company);
}
