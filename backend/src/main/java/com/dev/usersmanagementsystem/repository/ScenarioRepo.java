/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.repository;


import com.dev.usersmanagementsystem.entity.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScenarioRepo extends JpaRepository<Scenario, Integer> {
    @Query("SELECT s FROM Scenario s WHERE s.scenario_id = :scenarioId AND s.user_id = :userId")
    Optional<Scenario> findScenarioByScenario_idAndUser_id(@Param("scenarioId") int scenarioId, @Param("userId") int userId);
}
