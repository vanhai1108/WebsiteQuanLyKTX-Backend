package com.ktx.quanlykytucxa.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseMigrationConfig {

    @Bean
    public CommandLineRunner migrateDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE registrations MODIFY COLUMN status VARCHAR(255)");
                System.out.println("Successfully altered registrations status column to VARCHAR(255)");
            } catch (Exception e) {
                System.out.println("Could not alter registrations status column: " + e.getMessage());
            }
        };
    }
}
