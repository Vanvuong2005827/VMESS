package com.vuong.vmess.security.jwt_filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.vuong.vmess.base.RestData;
import com.vuong.vmess.constant.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @SneakyThrows
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String message = ErrorMessage.Auth.UNAUTHORIZED;
        Object body = RestData.error(message);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}

