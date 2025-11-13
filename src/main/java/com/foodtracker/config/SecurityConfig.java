package com.foodtracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for API endpoints
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/track").permitAll()  // Allow event tracking without auth
                        .requestMatchers("/api/events").permitAll() // Allow basic event queries
                        .requestMatchers("/api/events/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/analytics/**").permitAll()
                        // Require admin role for analytics
                        // .hasRole("ADMIN")
                        // .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {
                }); // Enable basic authentication

        return http.build();
    }
}