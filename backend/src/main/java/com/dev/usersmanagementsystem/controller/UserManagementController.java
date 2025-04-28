/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.controller;

import com.dev.usersmanagementsystem.dto.ReqRes;
import com.dev.usersmanagementsystem.entity.OurUsers;
import com.dev.usersmanagementsystem.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg) {
        return ResponseEntity.ok(usersManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(usersManagementService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody OurUsers reqres) {
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUSer(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }

    @PostMapping("/api/upload/json")
    public ResponseEntity<String> uploadJsonFile(@RequestParam("file") MultipartFile file, @RequestParam("id") String id, @RequestParam("code") String code, @RequestParam("desc") String desc) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty. Kindly select a different file and try again. Thanks", HttpStatus.BAD_REQUEST);
        }
        try {
            String jsonContent = new String(file.getBytes());
            usersManagementService.fileUpload(jsonContent, id, code, desc);
            return new ResponseEntity<>("File uploaded successfully!", HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error occurred while saving file" + e);
            return new ResponseEntity<>("Error processing the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/schedule/add")
    public ResponseEntity<ReqRes> addSchedule(@RequestParam("scenarioId") String scenarioId, @RequestParam("frequency") String frequency, @RequestParam("sdt") String sdt, @RequestParam("edt") String edt, @RequestParam("userId") String userId) {
        try {
            ReqRes response = usersManagementService.saveScheduleInfo(userId, scenarioId, frequency, sdt, edt);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error occurred while adding  schedule" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/schedule/stop-resume")
    public ResponseEntity<ReqRes> stopResumeSchedule(@RequestParam("scenarioId") String scenarioId, @RequestParam("userId") String userId, @RequestParam("state") String state) {
        try {
            ReqRes response = usersManagementService.stopResumeSchedule(userId, scenarioId, state);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error occurred while stopping schedule" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/get-execution-by-time")
    public ResponseEntity<ReqRes> getExecutionByTime(@RequestParam("utcMilliseconds") String utcMilliseconds) {
        try {
            ReqRes response = usersManagementService.getExecutionTime(Long.parseLong(utcMilliseconds));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error occurred while getting the execution time" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/get-scenario-by-userId")
    public ResponseEntity<ReqRes> getScenarioByUserId(@RequestParam("userId") String userId, @RequestParam("scenarioId") String scenarioId) {
        try {
            ReqRes response = usersManagementService.getScenarioByUserIdAndScenarioId(Integer.parseInt(userId), Integer.parseInt(scenarioId));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error occurred while getting the scenario by id" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api/get-company-by-userId")
    public ResponseEntity<ReqRes> getCompanyByUserId(@RequestParam("userId") String userId) {
        try {
            ReqRes reqRes = usersManagementService.getCompanyByUserId(Integer.parseInt(userId));
            return new ResponseEntity<>(reqRes, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error occurred while getting the company by user Id" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/mark-execution-status")
    public ResponseEntity<ReqRes> markExecutionStatus(@RequestParam("executionId") String executionId, @RequestParam("status") String status, @RequestParam("userId") String userId) {
        try {
            return ResponseEntity.ok(usersManagementService.saveExecutionStatus(executionId, status, userId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/api/store-logs")
    public ResponseEntity<ReqRes> storeLogs(@RequestParam("endTime") String endTime, @RequestParam("errorLog") String errorLog, @RequestParam("responseTime") String responseTime, @RequestParam("startTime") String startTime, @RequestParam("status") String status, @RequestParam("title") String title, @RequestParam("url") String url, @RequestParam("userId") String userId) {
        try {
            return ResponseEntity.ok(usersManagementService.saveLogs(endTime, errorLog, responseTime, startTime, status, title, url, userId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
