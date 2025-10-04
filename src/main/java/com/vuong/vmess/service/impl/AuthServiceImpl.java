package com.vuong.vmess.service.impl;

import com.vuong.vmess.constant.ErrorMessage;
import com.vuong.vmess.constant.RoleConstant;
import com.vuong.vmess.domain.dto.request.auth.LoginRequestDto;
import com.vuong.vmess.domain.dto.request.auth.RegisterRequestDto;
import com.vuong.vmess.domain.dto.response.auth.LoginResponseDto;
import com.vuong.vmess.domain.dto.response.auth.RegisterResponseDto;
import com.vuong.vmess.domain.entities.User;
import com.vuong.vmess.exception.extended.NotFoundException;
import com.vuong.vmess.exception.extended.UnauthorizedException;
import com.vuong.vmess.repository.RoleRepository;
import com.vuong.vmess.repository.UserRepository;
import com.vuong.vmess.security.UserPrincipal;
import com.vuong.vmess.service.AuthService;
import com.vuong.vmess.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            //set current user vào Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                    () -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME)
            );

            String accessToken = jwtUtils.generateToken(principal, Boolean.FALSE);
            String refreshToken = jwtUtils.generateToken(principal, Boolean.TRUE);

            redisTemplate.opsForValue().set("traceRefresh:" + accessToken, refreshToken, Duration.ofMinutes(60));

            log.info("Login successful with user ID: {}", user.getId());
            return new LoginResponseDto(accessToken, refreshToken, user.getId(), authentication.getAuthorities());
        } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_ACCOUNT);
        }
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UnauthorizedException(ErrorMessage.User.ERR_DUPLICATED_USERNAME);
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UnauthorizedException(ErrorMessage.User.ERR_DUPLICATED_EMAIL);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleRepository.findByRoleName(RoleConstant.USER))
                .email(request.getEmail())
                .updated_at(LocalDateTime.now())
                .build();

        User userSaved = userRepository.save(user);

        return RegisterResponseDto.builder()
                .role(userSaved.getRole().getName())
                .username(userSaved.getUsername())
                .email(userSaved.getEmail())
                .build();
    }

    @Override
    public String getAcTokenFromReToken(String refreshToken) {
        String accessToken = "";

        try {
            Authentication authentication = jwtUtils.getAuthenticationByRefreshToken(refreshToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            accessToken = jwtUtils.generateToken(userPrincipal, Boolean.FALSE);
        } catch (Exception e) {
            log.info("Some thing occur on getAcTokenFromReToken() method", e.getMessage());
            throw e;
        }
        return accessToken;
    }

    @Override
    public String logout(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = "";
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        long remainingTimeForAcToken = Duration.between(new Date().toInstant(), jwtUtils.extractExpirationFromJwt(token).toInstant()).toMinutes();

        String refreshToken = (String) redisTemplate.opsForValue().get("traceRefresh:" + token);

        long remainingTimeForReToken = Duration.between(new Date().toInstant(), jwtUtils.extractExpirationFromJwt(refreshToken).toInstant()).toMinutes();

        redisTemplate.opsForValue().set("blacklist:" + token, "", Duration.ofMinutes(remainingTimeForAcToken));
        redisTemplate.opsForValue().set("blacklist:" + refreshToken, "", Duration.ofMinutes(remainingTimeForReToken));



        return "Logout successful";
    }
}

