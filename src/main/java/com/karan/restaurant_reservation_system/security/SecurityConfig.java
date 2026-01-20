package com.karan.restaurant_reservation_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // ✅ PASSWORD ENCODER
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ CORS CONFIG (LOVABLE + LOCAL DEV)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://*.lovable.dev",
                "https://*.lovable.app"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        config.setExposedHeaders(List.of(
                "Authorization"
        ));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // ✅ SECURITY FILTER CHAIN
    @Bean
    public SecurityFilterChain filter(HttpSecurity http) throws Exception {

        http
                // ✅ ENABLE CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ DISABLE CSRF (JWT)
                .csrf(csrf -> csrf.disable())

                // ✅ DISABLE DEFAULT SPRING SECURITY LOGIN
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // ✅ STATELESS SESSION (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/reserve").permitAll()
                        .requestMatchers("/api/v1/admin/create-admin").hasRole("OWNER")
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("OWNER", "ADMIN")
                        .anyRequest().permitAll()
                );

        // ✅ JWT FILTER
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
