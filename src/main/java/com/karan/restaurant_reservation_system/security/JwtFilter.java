package com.karan.restaurant_reservation_system.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {

        String path = req.getRequestURI();

        // ✅ PUBLIC ENDPOINTS (NO AUTH NEEDED)
        if (path.startsWith("/api/v1/auth") ||
                path.startsWith("/api/v1/reserve")) {
            chain.doFilter(req, res);
            return;
        }

        // ✅ GET AUTHORIZATION HEADER
        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            // No token provided - let Spring Security handle it
            chain.doFilter(req, res);
            return;
        }

        try {
            // ✅ PARSE JWT TOKEN
            String token = header.substring(7);
            Claims claims = jwtUtil.parse(token);

            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            // ✅ CREATE AUTHENTICATION OBJECT
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            // ✅ SET AUTHENTICATION IN SECURITY CONTEXT
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            // Invalid token - clear context
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(req, res);
    }
}