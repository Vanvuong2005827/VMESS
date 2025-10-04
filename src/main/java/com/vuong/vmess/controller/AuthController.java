package com.vuong.vmess.controller;

import com.vuong.vmess.base.RestApiV1;
import com.vuong.vmess.base.VsResponseUtil;
import com.vuong.vmess.constant.UrlConstant;
import com.vuong.vmess.domain.dto.request.auth.LoginRequestDto;
import com.vuong.vmess.domain.dto.request.auth.RegisterRequestDto;
import com.vuong.vmess.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestApiV1
@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @Operation(summary = "API Login", description = "Anonymous")
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Inside login method of AuthController");
        return VsResponseUtil.success(authService.login(request));
    }

    @Operation(summary = "API register", description = "Anonymous")
    @PostMapping(UrlConstant.Auth.REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request) {
        log.info("Inside register method of AuthController");
        return VsResponseUtil.success(authService.register(request));
    }

    @Operation(summary = "API refreshToken", description = "Anonymous")
    @PostMapping(UrlConstant.Auth.REFRESHTOKEN)
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        log.info("Inside refreshToken method of AuthController");
        return VsResponseUtil.success(authService.getAcTokenFromReToken(refreshToken));
    }

    @Operation(summary = "API logout", description = "Authenticated")
    @GetMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        log.info("Inside logout method of AuthController");
        return VsResponseUtil.success(authService.logout(request));
    }
}
