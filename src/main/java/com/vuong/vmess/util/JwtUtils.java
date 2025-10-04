package com.vuong.vmess.util;

import com.vuong.vmess.constant.ErrorMessage;
import com.vuong.vmess.exception.extended.InvalidException;
import com.vuong.vmess.security.UserPrincipal;
import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtUtils {
    private final String CLAIM_TYPE = "type";
    private final String TYPE_ACCESS = "access";
    private final String TYPE_REFRESH = "refresh";
    private final String USERNAME_KEY = "username";
    private final String AUTHORITIES_KEY = "auth";

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.access.expiration_time}")
    private Integer EXPIRATION_TIME_ACCESS_TOKEN;

    @Value("${jwt.refresh.expiration_time}")
    private Integer EXPIRATION_TIME_REFRESH_TOKEN;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String generateToken(UserPrincipal userPrincipal, Boolean isRefreshToken) {
        String authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Map<String, Object> claim = new HashMap<>();
        claim.put(CLAIM_TYPE, isRefreshToken ? TYPE_REFRESH : TYPE_ACCESS);
        claim.put(USERNAME_KEY, userPrincipal.getUsername());
        claim.put(AUTHORITIES_KEY, authorities);
        if (isRefreshToken) {

            return Jwts.builder()
                    .setClaims(claim)
                    .setSubject(userPrincipal.getId().toString())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + (EXPIRATION_TIME_REFRESH_TOKEN * 60 * 1000L)))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();
        }
        return Jwts.builder()
                .setClaims(claim)
                .setSubject(userPrincipal.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (EXPIRATION_TIME_ACCESS_TOKEN * 60 * 1000L)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public Authentication getAuthenticationByRefreshToken(String refreshToken) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(refreshToken)
                .getBody();

        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE))
                || ObjectUtils.isEmpty(claims.get(AUTHORITIES_KEY))
                || ObjectUtils.isEmpty(claims.get(USERNAME_KEY))
                || redisTemplate.hasKey("blacklist:" + refreshToken)
        ) {
            throw new InvalidException(ErrorMessage.Auth.INVALID_REFRESH_TOKEN);
        }

        String subject = claims.getSubject();
        String username = claims.get(USERNAME_KEY, String.class);
        String authStr  = claims.get(AUTHORITIES_KEY, String.class);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authStr.split(","))
                        .filter(s -> !s.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        var principal = new UserPrincipal(
                UUID.fromString(subject),
                username,
                null,
                authorities
        );
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public String extractClaimUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().get(USERNAME_KEY).toString();
    }

    public String extractSubjectFromJwt(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public Date extractExpirationFromJwt(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration();
    }

    public String extractAuthoritiesString(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("auth", String.class);
    }


    public Boolean isTokenExpired(String token) {
        return extractExpirationFromJwt(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !redisTemplate.hasKey("blacklist:" + token);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

}

