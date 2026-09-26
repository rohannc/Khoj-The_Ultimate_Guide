package com.rohan.Khoj.security;

import com.rohan.Khoj.common.Role;
import com.rohan.Khoj.security.JwtAuthFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication/authorization required)
                        .requestMatchers(
                                "/api/auth/**",          // login, register, refresh, logout
                                "/error",                // Spring Boot error dispatch
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/clinics/**",       // All clinic read-only paths
                                "/api/doctors/**",       // All doctor read-only paths
                                "/docs",
                                "/h2-console/**"
                        ).permitAll()

                        // Appointments can be booked/viewed by patients, and updated/managed by clinics & doctors
                        .requestMatchers(
                                "/api/appointments/**"
                        ).hasAnyAuthority(Role.ROLE_PATIENT.name(), Role.ROLE_CLINIC.name(), Role.ROLE_DOCTOR.name())

                        // Prescriptions: patients can view and update start-date, doctors can view/manage
                        .requestMatchers(
                                "/api/prescriptions/**"
                        ).hasAnyAuthority(Role.ROLE_PATIENT.name(), Role.ROLE_DOCTOR.name())

                        // Patients can view/update their own profile
                        .requestMatchers(
                                "/api/patients/{id}/**"
                        ).hasAnyAuthority(Role.ROLE_PATIENT.name())

                        // Doctors can manage their own profile and affiliations
                        .requestMatchers(
                                "/api/doctor/affiliations/**",
                                "/api/doctors/{id}/**"
                        ).hasAnyAuthority(Role.ROLE_DOCTOR.name())

                        // Clinics can manage their own profile and affiliations
                        .requestMatchers(
                                "/api/clinic/affiliations/**",
                                "/api/clinics/{id}/**"
                        ).hasAnyAuthority(Role.ROLE_CLINIC.name())

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Configures the Authentication Provider (DaoAuthenticationProvider)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // Provides the PasswordEncoder bean for hashing passwords (e.g., BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Allow all origins for dev. In prod, specify the frontend URL.
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}