package com.ducdv38.springsecurity.config;

import com.ducdv38.springsecurity.entity.Role;
import com.ducdv38.springsecurity.entity.User;
import com.ducdv38.springsecurity.repository.RoleRepository;
import com.ducdv38.springsecurity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner runner(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseGet(() -> roleRepository.save(Role.builder()
                                .name("ADMIN")
                                .permissions(null)
                                .description("Admin role")
                                .build()));

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created successfully with default password: admin");
            }
        };
    }
}
