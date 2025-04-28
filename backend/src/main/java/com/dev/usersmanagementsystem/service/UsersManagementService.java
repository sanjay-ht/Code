/*
Author: Ankit Kumar Sharma
 */
package com.dev.usersmanagementsystem.service;

import com.dev.usersmanagementsystem.App;
import com.dev.usersmanagementsystem.dto.ReqRes;
import com.dev.usersmanagementsystem.entity.*;
import com.dev.usersmanagementsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class UsersManagementService {
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private ScenarioRepo scenarioRepo;
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private ExecutionTimeRepo executionTimeRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        try {
            OurUsers ourUser = new OurUsers();
            Company company = new Company();
            String userDb = registrationRequest.getRole().toUpperCase();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            OurUsers ourUsersResult = usersRepo.save(ourUser);
            if (!userDb.equals("ADMIN")) {
                String createDatabaseQuery = "CREATE DATABASE " + userDb;
                jdbcTemplate.execute(createDatabaseQuery);
                System.out.println("Database '" + userDb + "' created successfully.");
                String useDatabaseQuery = "USE " + userDb;
                jdbcTemplate.execute(useDatabaseQuery);
                System.out.println("Using database: " + userDb);
                String createTableQuery = "CREATE TABLE response_time ("
                        + " name VARCHAR(50),"
                        + "time timestamp , "
                        + "End_time timestamp   , "
                        + "ErrorLog TEXT, "
                        + "Response_time INT, "
                        + "Start_time timestamp   , "
                        + "Status VARCHAR(50), "
                        + "Title VARCHAR(255), "
                        + "URL VARCHAR(255));";

                jdbcTemplate.execute(createTableQuery);
                String userManagement = "USE " + "ubiurls";
                jdbcTemplate.execute(userManagement);
                System.out.println("Table 'response_time' created successfully.");
            }

            if (ourUsersResult.getId() > 0) {
                company.setCompanyId(ourUsersResult.getId());
                company.setDbName(registrationRequest.getName().toUpperCase());
                company.setPassword("root");
                company.setUsername("root");
                company.setDbHost("127.0.0.1");
                companyRepo.save(company);
                resp.setOurUsers((ourUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        try {
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<OurUsers> result = usersRepo.findAllUsers();
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes fileUpload(String json, String id, String code, String desc) throws Exception {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(Integer.parseInt(id));
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                List<Scenario> scenarioList = existingUser.getScenarios();
                Scenario scenario = new Scenario();
                scenario.setUser_id(existingUser.getId());
                scenario.setJsonFile(json);
                scenario.setCode(code);
                scenario.setDescription(desc);
                scenario.setStatus("Active");
                scenarioList.add(scenario);
                scenarioRepo.saveAll(scenarioList);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred saving user json file: " + e.getMessage());
            throw new Exception("Error occurred while saving json file");

        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, OurUsers updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
                OurUsers savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }

    public ReqRes saveScheduleInfo(String userId, String scenarioId, String frequency, String startDateTime, String endDateTime) {
        ReqRes reqRes = new ReqRes();
        LocalDateTime startLocalDateTime = LocalDateTime.parse(startDateTime, formatter);
        LocalDateTime endLocalDateTime = LocalDateTime.parse(endDateTime, formatter);

        long startTimeInMilli = startLocalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        long endTimeInMilli = endLocalDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        try {
            Optional<Scenario> scenarioOptional = scenarioRepo.findScenarioByScenario_idAndUser_id(Integer.parseInt(scenarioId), Integer.parseInt(userId));
            if (scenarioOptional.isPresent()) {
                Scenario scenario = scenarioOptional.get();
                Schedule schedule = new Schedule();
                schedule.setSchedule_id(Integer.parseInt(scenarioId));
                schedule.setFrequency(Integer.parseInt(frequency));
                schedule.setStartTimeInMillis(startTimeInMilli);
                schedule.setEndTimeInMillis(endTimeInMilli);
                schedule.setUserId(scenario.getUser_id());
                scheduleRepo.save(schedule);
                List<ExecutionTime> executionTimes = new ArrayList<>();
                long frequencyInMillis = (long) Integer.parseInt(frequency) * 60 * 1000;
                while (startTimeInMilli <= endTimeInMilli) {
                    ExecutionTime executionTime = new ExecutionTime();
                    executionTime.setStartTimeInMillis(startTimeInMilli);
                    executionTime.setScenarioId(Integer.parseInt(scenarioId));
                    executionTime.setUserId(Integer.parseInt(userId));
                    executionTime.setStatus("Active");
                    executionTimes.add(executionTime);
                    startTimeInMilli += frequencyInMillis;
                }
                executionTimeRepo.saveAll(executionTimes);
                scenario.setSchedule(schedule);
                scenarioRepo.save(scenario);
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");

            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occured while saving schedule: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return reqRes;
    }

    public ReqRes stopResumeSchedule(String userId, String scenarioId, String state) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Scenario> scenario = scenarioRepo.findScenarioByScenario_idAndUser_id(Integer.parseInt(scenarioId), Integer.parseInt(userId));
            if (scenario.isPresent()) {
                scenario.get().setStatus(state);
                scheduleRepo.save(scenario.get().getSchedule());
            }

            List<ExecutionTime> executionTime = executionTimeRepo.findExecutionTimeByScenarioIdAndUserId(Integer.parseInt(scenarioId), Integer.parseInt(userId));
            for (ExecutionTime executionTime1 : executionTime) {
                if (executionTime1.getStatus().equals("Active") || executionTime1.getStatus().equals("Inactive")) {
                    executionTime1.setStatus(state);
                }
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            }
            executionTimeRepo.saveAll(executionTime);

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occured while stopping schedule: " + e.getMessage());
            throw new RuntimeException(e);

        }
        return reqRes;
    }

    //    @Scheduled(fixedRate = 60000)
    public void scheduleExecution() {
        Long currentTimeInMillis = Instant.now().toEpochMilli() + 19800000;
        Instant instant = Instant.ofEpochMilli(currentTimeInMillis);
        ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("UTC"));
        utcDateTime = utcDateTime.withSecond(0).withNano(0);
        long utcMilliseconds = utcDateTime.toInstant().toEpochMilli();
        System.out.println("Hello===" + utcMilliseconds);
        List<ExecutionTime> executionTimes = executionTimeRepo.findByStartTimeInMillis(utcMilliseconds);
        System.out.println("Execution time" + executionTimes);
        for (ExecutionTime executionTime : executionTimes) {
            Optional<Scenario> scenario = scenarioRepo.findScenarioByScenario_idAndUser_id(executionTime.getScenarioId(), executionTime.getUserId());
            System.out.println("New scenario" + scenario);
            if (scenario.isPresent() && executionTime.getStatus().equals("Active")) {
                String jsonContent = scenario.get().getJsonFile();
                Optional<Company> company = companyRepo.findByCompanyId(executionTime.getUserId());
                String password = "";
                String username = "";
                String dbhost = "";
                String dbName = "";
                if (company.isPresent()) {
                    password = company.get().getPassword();
                    username = company.get().getUsername();
                    dbhost = company.get().getDbHost();
                    dbName = company.get().getDbName();
                }
                App obj = new App();
                try {
                    executionTime.setStatus("Executed");
                    executionTimeRepo.save(executionTime);
                    obj.setup(password, username, dbhost, dbName);
                    obj.runCode(jsonContent);

                } catch (Exception e) {
                    System.out.println("Exception occurred while executing json" + e);
                }
                System.out.println("Time match" + scenario.get().getJsonFile());
            }
        }

    }

    public ReqRes getExecutionTime(long utcMilliseconds) {
        ReqRes reqRes = new ReqRes();
        try {
            List<ExecutionTime> executionTimes = executionTimeRepo.findByStartTimeInMillis(utcMilliseconds);
            if (!executionTimes.isEmpty()) {
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
                reqRes.setExecutionTimeList(executionTimes);
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting execution time: " + e.getMessage());
            System.out.println("Exception Occurred while getting execution time list from milliseconds" + e);
        }
        return reqRes;

    }

    public ReqRes getScenarioByUserIdAndScenarioId(int userId, int scenarioId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Scenario> scenario = scenarioRepo.findScenarioByScenario_idAndUser_id(scenarioId, userId);
            if (scenario.isPresent()) {
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
                reqRes.setScenario(scenario.get());
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting scenario: " + e.getMessage());
            System.out.println("Exception Occurred while getting scenario from userId and scenarioId" + e);
        }
        return reqRes;
    }

    public ReqRes getCompanyByUserId(int userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Company> company = companyRepo.findByCompanyId(userId);
            if (company.isPresent()) {
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
                reqRes.setCompany(company.get());
            }

        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting company: " + e.getMessage());
            System.out.println("Exception Occurred while getting company from userId" + e);
        }
        return reqRes;
    }

    public ReqRes saveExecutionStatus(String executionIdParam, String status, String userId) {
        ReqRes response = new ReqRes();
        try {
            int executionId = Integer.parseInt(executionIdParam);
            ExecutionTime executionTime = executionTimeRepo.findExecutionTimeByExecutionIdAndUserId(executionId, Integer.parseInt(userId));
            executionTime.setStatus(status);
            executionTimeRepo.save(executionTime);
            response.setStatusCode(200);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while saving execution status: " + e.getMessage());
            System.out.println("Exception Occurred while saving execution status" + e);

        }
        return response;
    }

    public ReqRes saveLogs(String endTime, String errorLog, String responseTime, String startTime, String status, String title, String url, String userId) {
        ReqRes response = new ReqRes();
        try {
            Timestamp endTimeVal = Timestamp.valueOf(endTime);
            long responseTimeVal = Long.valueOf(responseTime);
            Timestamp startTimeVal = Timestamp.valueOf(startTime);
            Optional<Company> company = companyRepo.findByCompanyId(Integer.parseInt(userId));
            String password = "";
            String username = "";
            String dbhost = "";
            String dbName = "";
            if (company.isPresent()) {
                if (company != null) {
                    password = company.get().getPassword();
                    username = company.get().getUsername();
                    dbhost = company.get().getDbHost();
                    dbName = company.get().getDbName();
                }
            }
            dbhost = "localhost";
            String dburl = "jdbc:mysql://" + dbhost + ":" + "3306";
            Connection conn = connectToMySQL(dburl, username, password, dbName);
            insertResponseTimeData(conn, endTimeVal, errorLog, responseTimeVal, startTimeVal, status, title, url);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while saving logs: " + e.getMessage());
            throw new RuntimeException(e);
        }


        return response;
    }

    public static Connection connectToMySQL(String url, String username, String password, String dbName) {
        try {
            String jdbcUrl = url + "/" + dbName;
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertResponseTimeData(Connection conn, Timestamp endTime, String errorLog, long responseTime,
                                       Timestamp startTime, String status, String title, String url) {
        String insertQuery = "INSERT INTO response_time (name,time,End_time, ErrorLog, Response_time, Start_time, Status, Title, URL) "
                + "VALUES (? ,?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, "");
            stmt.setTimestamp(2, endTime);
            stmt.setTimestamp(3, endTime);
            stmt.setString(4, errorLog);
            stmt.setLong(5, responseTime);
            stmt.setTimestamp(6, startTime);
            stmt.setString(7, status);
            stmt.setString(8, title);
            stmt.setString(9, url);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inserting data into response_time table: " + e.getMessage());
        }
    }
}
