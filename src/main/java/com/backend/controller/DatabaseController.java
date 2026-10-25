package com.backend.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DatabaseController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/db-test")
    public String databaseTest() {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT 1",
                Integer.class
        );

        return "MySQL connected successfully! Result = " + result;
    }
}