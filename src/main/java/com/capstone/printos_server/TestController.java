package com.capstone.printos_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test")
    public Map<String, Object> testEndpoint() {
        Map<String, Object> response = new HashMap<>();

        // Query MySQL for current time
        String sql = "SELECT NOW()";
        String dbTime = jdbcTemplate.queryForObject(sql, String.class);

        response.put("message", "Connected to database!");
        response.put("currentDbTime", dbTime);

        return response;
    }

}

