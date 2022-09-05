package com.baling.repository.administration;

import com.baling.models.administration.Company;
import com.baling.models.administration.CompanyRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface CompanyRefundRepository extends JpaRepository<CompanyRefund, Integer> {
    List<CompanyRefund> findByCompanyOrderByLeftHour(Company company);
    Boolean existsByLeftHour(double leftHour);
    List<CompanyRefund> findByLeftHourAndCompany(double leftHour,Company company);
}
