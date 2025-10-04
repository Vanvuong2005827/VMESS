package com.vuong.vmess.service;

import com.vuong.vmess.domain.dto.request.auth.LoginRequestDto;
import com.vuong.vmess.domain.dto.request.auth.RegisterRequestDto;
import com.vuong.vmess.domain.dto.response.auth.LoginResponseDto;
import com.vuong.vmess.domain.dto.response.auth.RegisterResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);
    RegisterResponseDto register(RegisterRequestDto request);
    String getAcTokenFromReToken(String refreshToken);

    String logout(HttpServletRequest request);
}
