package com.vuong.vmess.security.rate_limit;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RateLimitPolicy {
    String pattern;
    long capacity;
    long refillTokens;
    Duration refillPeriod;
}
