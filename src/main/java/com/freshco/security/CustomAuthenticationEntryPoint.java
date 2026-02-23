package com.freshco.security;

import com.freshco.exception.JwtAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        int status = HttpStatus.UNAUTHORIZED.value();

        if (authException instanceof JwtAuthenticationException jwtException) {
            status = jwtException.getStatus().value();
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(status),
                authException.getMessage()
        );

        problemDetail.setTitle("Authentication failure");
        problemDetail.setType(URI.create("https://api.freshco.com/errors/authentication"));
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("timestamp", Instant.now());

        objectMapper.writeValue(response.getOutputStream(), problemDetail);
    }

}
