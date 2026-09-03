package com.support.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class WebhookAuthFilter extends OncePerRequestFilter {

    @Value("${webhook.secret}")
    private String expectedSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String expected = "Bearer " + expectedSecret;

        if (authHeader == null || !authHeader.equals(expected)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                    "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\"," +
                    "\"message\":\"Missing or invalid webhook secret\"}",
                    Instant.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }
}