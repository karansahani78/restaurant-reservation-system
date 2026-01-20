package com.karan.restaurant_reservation_system.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
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

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            Claims claims = jwtUtil.parse(header.substring(7));
            String role = claims.get("role", String.class);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}
