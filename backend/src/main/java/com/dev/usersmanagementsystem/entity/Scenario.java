/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Scenario")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scenario_id;
    @Column
    private String code;
    @Column
    private String description;
    @Column(columnDefinition = "json")
    private String jsonFile;
    @Column
    private String status;
    @Column
    private int user_id;
    @OneToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
}
