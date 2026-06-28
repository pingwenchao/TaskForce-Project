package com.upm.taskforce.config;

import com.upm.taskforce.entity.Role;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central security configuration.
 * Defines PasswordEncoder, filter chain, and method-level security.
 * AuthenticationManager is auto-configured.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * BCrypt password encoder as a static bean to avoid circular dependency
     * on the SecurityConfig instance during initialization.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean for potential use in controllers.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines the security filter chain:
     * - Public pages: login, register.
     * - Static resources are automatically permitted.
     * - All other requests require authentication.
     * - Custom login page with default success redirect to /dashboard.
     * - Session fixation protection enabled.
     * - Logout invalidates session, clears cookies, and redirects to login.
     * - CSRF protection enabled by default.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/dashboard").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/projects/**").hasRole("ADMIN")
                        .requestMatchers("/tasks/new", "/tasks/add").hasRole("ADMIN")
                        .requestMatchers("/tasks/{id}/status").hasAnyRole("ADMIN", "EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/error/403")
                );
        return http.build();
    }
}