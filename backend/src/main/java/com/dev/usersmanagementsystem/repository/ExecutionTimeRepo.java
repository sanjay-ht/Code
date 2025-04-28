/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.repository;

import com.dev.usersmanagementsystem.entity.ExecutionTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecutionTimeRepo extends JpaRepository<ExecutionTime, Long> {
    List<ExecutionTime> findByStartTimeInMillis(Long currentTimeInMillis);

    ExecutionTime findExecutionTimeByExecutionIdAndUserId(Integer id, Integer userId);

    @Query("SELECT e FROM ExecutionTime e WHERE e.scenarioId = :scenarioId AND e.userId = :userId")
    List<ExecutionTime> findExecutionTimeByScenarioIdAndUserId(@Param("scenarioId") int scenarioId, @Param("userId") int userId);
}
