/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepo extends JpaRepository<Schedule, Integer> {

}
