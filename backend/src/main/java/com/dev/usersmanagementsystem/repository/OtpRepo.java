package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepo extends JpaRepository<Otp, String> {

}
