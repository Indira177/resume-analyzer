package com.resumeanalyzer.config;

import com.resumeanalyzer.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final PasswordEncoder passwordEncoder;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder) {

        this.customUserDetailsService =
                customUserDetailsService;

        this.passwordEncoder =
                passwordEncoder;
    }


    // =========================================================
    // AUTHENTICATION PROVIDER
    // =========================================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        customUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                // -------------------------------------------------
                // CSRF
                // -------------------------------------------------

                .csrf(csrf ->
                        csrf.disable()
                )


                // -------------------------------------------------
                // SESSION
                // -------------------------------------------------

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )


                // -------------------------------------------------
                // AUTHORIZATION
                // -------------------------------------------------

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // PUBLIC AUTHENTICATION APIs
                        // =================================================

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register"
                        )
                        .permitAll()


                        // =================================================
                        // HEALTH
                        // =================================================

                        .requestMatchers(
                                "/api/health"
                        )
                        .permitAll()


                        // =================================================
                        // FRONTEND
                        // =================================================

                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/style.css",
                                "/script.js",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()


                        // =================================================
                        // PROTECTED AUTH APIs
                        // =================================================

                        .requestMatchers(
                                "/api/auth/me",
                                "/api/auth/logout"
                        )
                        .authenticated()


                        // =================================================
                        // PROTECTED RESUME APIs
                        // =================================================

                        .requestMatchers(
                                "/api/resumes/**"
                        )
                        .authenticated()


                        // =================================================
                        // PROTECTED JOB MATCHING APIs
                        // =================================================

                        .requestMatchers(
                                "/api/job-match/**"
                        )
                        .authenticated()


                        // =================================================
                        // PROTECTED ATS ANALYSIS APIs
                        // =================================================

                        .requestMatchers(
                                "/api/analysis/**"
                        )
                        .authenticated()


                        // =================================================
                        // PROTECTED AI ANALYSIS APIs
                        // =================================================

                        .requestMatchers(
                                "/api/ai/**"
                        )
                        .authenticated()


                        // =================================================
                        // PROTECTED JOB DESCRIPTION APIs
                        // =================================================

                        .requestMatchers(
                                "/api/job-descriptions/**"
                        )
                        .authenticated()


                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest()
                        .authenticated()
                );


        return http.build();
    }
}