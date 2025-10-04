package com.vuong.vmess.security.rate_limit;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {
    private static final String KEY_PREFIX = "rate-limit:";
    private final ProxyManager<String> proxyManager;
    private final List<RateLimitPolicy> policies;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        RateLimitPolicy p = match(req.getRequestURI());
        if (p == null) {
            chain.doFilter(req, res);
            return;
        }

        String key = resolveKey(req);
        String bucketKey = KEY_PREFIX + p.getPattern() + "|" + key;

        Bucket bucket = proxyManager.builder().build(bucketKey, () -> toConfig(p));

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {

            res.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            chain.doFilter(req, res);
        } else {
            long nanos = probe.getNanosToWaitForRefill();
            long secs = Math.max(1, Duration.ofNanos(nanos).toSeconds());

            res.setStatus(429);
            res.setContentType("application/json;charset=UTF-8");
            res.setHeader("Retry-After", String.valueOf(secs));
            String body = """
              {"status":429,"message":"Too Many Requests","retryAfterSeconds":%d}""".formatted(secs);
            res.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private RateLimitPolicy match(String path) {
        for (RateLimitPolicy p : policies) if (matcher.match(p.getPattern(), path)) return p;
        return null;
    }

    private BucketConfiguration toConfig(RateLimitPolicy p) {
        Bandwidth limit = Bandwidth.classic(
                p.getCapacity(),
                Refill.greedy(p.getRefillTokens(), p.getRefillPeriod())
        );
        return BucketConfiguration.builder().addLimit(limit).build();
    }

    private String resolveKey(HttpServletRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            return "USER:" + auth.getName();
        }
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return "IP:" + xf.split(",")[0].trim();
        String xr = req.getHeader("X-Real-IP");
        if (xr != null && !xr.isBlank()) return "IP:" + xr.trim();
        return "IP:" + req.getRemoteAddr();
    }
}