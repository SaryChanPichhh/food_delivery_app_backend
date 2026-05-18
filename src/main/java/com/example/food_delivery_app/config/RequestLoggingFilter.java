package com.example.food_delivery_app.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request);

        filterChain.doFilter(wrappedRequest, response);

        String body = new String(
                wrappedRequest.getContentAsByteArray(),
                StandardCharsets.UTF_8
        );

        log.info("""
                API Request
                Method : {}
                URI    : {}
                Body   : {}
                """,
                request.getMethod(),
                request.getRequestURI(),
                body
        );
    }
}
