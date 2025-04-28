/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepo extends JpaRepository<Company, Integer> {
    @Query("SELECT c FROM Company c WHERE c.companyId = :companyId")
    Optional<Company> findByCompanyId(@Param("companyId") int companyId);
}
