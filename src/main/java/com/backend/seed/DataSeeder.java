package com.backend.seed;

import com.backend.entity.Admin;
import com.backend.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates one default admin account on first startup, so there is always
 * a way to log in to the admin dashboard.
 *
 *   email:    admin@farmfresh.com
 *   password: admin123
 *
 * Change the password from admin.html profile settings (or directly in the
 * database) after first login.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setFullName("Admin");
            admin.setEmail("admin@farmfresh.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
            System.out.println(">>> Default admin created: admin@farmfresh.com / admin123");
        }
    }
}
