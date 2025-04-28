/*
Author: Ankit Kumar Sharma
*/
package com.dev.usersmanagementsystem;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;


public class App {
    private WebDriver driver;
    private Connection conn;

    public void setup(String password, String username, String dbHost, String dbName) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions opt = new EdgeOptions();
        opt.addArguments("--remote-allow-origins=*");
        driver = new EdgeDriver(opt);
        dbHost = "localhost";
        String url = "jdbc:mysql://" + dbHost + ":" + "3306";
        try {
            conn = connectToMySQL(url, username, password, dbName);
            if (conn != null) {
                System.out.println("Connection successful!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
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
            System.out.println("Rows affected: " + rowsAffected);  // Output the number of rows inserted
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inserting data into response_time table: " + e.getMessage());
        }
    }

    public int getNodeLength(JsonNode eventsNode) {
        int count = 0;
        if (eventsNode.isArray() && eventsNode != null) {
            for (JsonNode ignored : eventsNode) {
                count++;
            }
        }
        return count;
    }

    private void waitForElementVisibility(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(locator)));
    }

    private void enterText(By Selector, String text) {
        WebElement element = driver.findElement(Selector);
        if (element != null) {
            element.clear();
            element.sendKeys(text);
        }

    }

    private void navigateAndWait(String url) {
        System.out.println("Url: " + url);
        if (url != null) {
            driver.get(url);
        }
        waitForElementVisibility(By.xpath("/html/body"));
    }

    private void clickOnElement(By locator) {
        waitForElementVisibility(locator);
        WebElement button = driver.findElement(locator);
        button.click();

    }

    public void runCode(String jsonContent) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        JsonNode stepsNode = rootNode.get("steps");
        System.out.println("Steps: " + stepsNode);
        int nodelen = getNodeLength(stepsNode);
        System.out.println("Length of Steps are " + nodelen);

        for (JsonNode step : stepsNode) {
            System.out.println("Step: " + step);
            String type = step.get("type").asText();
            if (type.equals("navigate")) {
                String url = step.get("url").asText();
                JsonNode assertedEventsNode = step.get("assertedEvents");
                String title = "";
                if (assertedEventsNode != null && !assertedEventsNode.isEmpty()) {
                    title = assertedEventsNode.get(0).get("title").asText();
                    System.out.println("Title: " + title);
                }
                try {
                    System.out.println("Navigating to URL: " + url);
                    long startTime = System.currentTimeMillis();
                    Timestamp startTimeStamp = new Timestamp(startTime);
                    navigateAndWait(url);
                    long endTime = System.currentTimeMillis();
                    Timestamp endTimeStamp = new Timestamp(endTime);
                    insertResponseTimeData(conn, endTimeStamp, "", endTime - startTime, startTimeStamp, "Success", title, url);
                } catch (Exception e) {
                    Timestamp zeroTimestamp = new Timestamp(0);
                    insertResponseTimeData(conn, zeroTimestamp, e.toString(), 0, zeroTimestamp, "Failed", title, url);

                }

            }
            if (type.equals("click")) {
                JsonNode selectorsGroup = step.get("selectors");
                for (JsonNode selectors : selectorsGroup) {
                    for (JsonNode selector : selectors) {
                        String SelectorText = selector.asText();
                        System.out.println("Selector: " + SelectorText);
                        try {
                            if (SelectorText.startsWith("xpath")) {
                                String xpath = SelectorText.replace("xpath/", "");
                                System.out.println(xpath);
                                waitForElementVisibility(By.xpath(xpath));
                                clickOnElement(By.xpath(xpath));
                                break;
                            }
                        } catch (NoSuchElementException e) {   //break;
                            String regex = "[^\"]+component-[^\"]+";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(SelectorText);
                            System.out.println("Xpath Didn't Worked Trying With Css Selector");
                            if (matcher.find()) {
                                String csspath = matcher.group(); // Extracted ID part
                                csspath = "#" + csspath;
                                System.out.println("Extracted ID: " + csspath);
                                waitForElementVisibility(By.cssSelector(csspath));
                                clickOnElement(By.cssSelector(csspath));
                            } else {
                                System.out.println("ID not found.");
                            }

                        }
                    }

                }
            }
            if (type.equals("change")) {
                JsonNode selectorsGroup = step.get("selectors");
                for (JsonNode selectors : selectorsGroup) {
                    for (JsonNode selector : selectors) {
                        String SelectorText = selector.asText();
                        System.out.println("Selector: " + SelectorText);
                        try {
                            if (SelectorText.startsWith("xpath")) {
                                String xpath = SelectorText.replace("xpath/", "");
                                System.out.println(xpath);
                                String Text = step.get("value").asText();
                                waitForElementVisibility(By.xpath(xpath));
                                enterText(By.xpath(xpath), Text);
                                break;
                            }

                        } catch (Exception e) {
                            String errorLog = e.toString();
                            System.out.println(errorLog);
                        }


                    }
                }
            }

        }

    }
}
