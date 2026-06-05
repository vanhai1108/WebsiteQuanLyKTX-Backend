package com.ktx.quanlykytucxa.config;

import com.ktx.quanlykytucxa.entities.User;
import com.ktx.quanlykytucxa.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class PasswordMigrationConfig {

    @Bean
    public CommandLineRunner migratePasswords(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            List<User> users = userRepository.findAll();
            int updated = 0;

            for (User user : users) {
                String rawPassword = user.getPassword();
                if (rawPassword == null || rawPassword.isEmpty()) {
                    continue;
                }


                if (rawPassword.startsWith("$2a$") || rawPassword.startsWith("$2b$") || rawPassword.startsWith("$2y$")) {
                    continue;
                }

                user.setPassword(passwordEncoder.encode(rawPassword));
                updated++;
            }

            if (updated > 0) {
                userRepository.saveAll(users);
                System.out.println("Password migration completed. Updated " + updated + " user passwords to BCrypt.");
            } else {
                System.out.println("Password migration skipped. No plaintext passwords detected.");
            }
        };
    }
}

