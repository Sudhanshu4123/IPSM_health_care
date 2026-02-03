package com.ipsm.backend.config;

import com.ipsm.backend.entity.User;
import com.ipsm.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            Optional<User> adminOpt = userRepository.findByUsername("admin");

            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                // Check if password is plain 'admin' or starts with $2a$ (BCrypt)
                // Basic check: if it is "admin", we hash it.
                if ("admin".equals(admin.getPassword())) {
                    System.out.println("Migrating Admin Password to BCrypt...");
                    admin.setPassword(passwordEncoder.encode("admin"));
                    userRepository.save(admin);
                }
            } else {
                System.out.println("Creating Default Admin User...");
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ADMIN");
                admin.setDepartment("IT");
                // Set default permissions
                admin.setRegNew(true);
                admin.setRegEdit(true);
                admin.setRegManage(true);
                admin.setInvStatus(true);
                admin.setInvReprint(true);
                admin.setRepOutstanding(true);
                admin.setRepSummary(true);
                admin.setRepLedger(true);
                admin.setRepBusiness(true);
                admin.setRepSales(true);
                admin.setTestStatus(true);

                userRepository.save(admin);
            }
        };
    }
}
