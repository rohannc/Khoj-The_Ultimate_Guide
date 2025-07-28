package com.rohan.Khoj.config;

import com.rohan.Khoj.entity.Role;
import com.rohan.Khoj.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
@EnableMethodSecurity // Enables method-level security (e.g., @PreAuthorize)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService; // Our custom UserDetailsService

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication/authorization required)
                        .requestMatchers(
                                "/api/auth/register/**",
                                "/api/auth/login",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api/clinics",          // Publicly viewable list of all clinics
                                "/api/clinics/{id}",     // Publicly viewable specific clinic
                                "/api/clinics/by-name",
                                "/api/clinics/by-city",
                                "/api/clinics/by-pin-code",
                                "/api/clinics/by-email/{emailId}",
                                "/api/doctors",          // Publicly viewable list of all doctors
                                "/api/doctors/{id}",     // Publicly viewable specific doctor
                                "/api/doctors/by-username/{username}",
                                "/api/doctors/by-email/{emailId}",
                                "/api/doctors/by-specialization",
                                "/api/doctors/by-last-name"
                        ).permitAll()

                        // --- Role-Based Authorization Rules ---
                        // Patients can view/update their own profile and manage their appointments
                        .requestMatchers(
                                "/api/patients/{id}/**", // Can access specific patient profile paths
                                "/api/appointments/**"   // Can access all appointment paths
                        ).hasAnyAuthority(Role.ROLE_PATIENT.name()) // Only PATIENT role

                        // Doctors can view/update their own profile and manage affiliations
                        .requestMatchers(
                                "/api/doctors/{id}/**", // Can access specific doctor profile paths
                                "/api/doctors/affiliations/**", // All affiliation paths
                                "/api/doctors/{doctorId}/affiliations/clinic/{clinicId}",
                                "/api/doctors/{doctorId}/clinics"
                        ).hasAnyAuthority(Role.ROLE_DOCTOR.name()) // Only DOCTOR role

                        // Clinics can view/update their own profile and potentially manage doctors
                        .requestMatchers(
                                "/api/clinics/{id}/**" // Can access specific clinic profile paths
                        ).hasAnyAuthority(Role.ROLE_CLINIC.name()) // Only CLINIC role

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Configures the Authentication Provider (DaoAuthenticationProvider)
    // It uses our UserDetailsService to load user details and PasswordEncoder to verify passwords.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // Provides the PasswordEncoder bean for hashing passwords (e.g., BCrypt)
    // This should be the same encoder used during user registration.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Exposes the AuthenticationManager bean. This is used in the login endpoint
    // to programmatically authenticate users.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}