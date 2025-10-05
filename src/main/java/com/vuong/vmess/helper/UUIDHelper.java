package com.vuong.vmess.helper;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Component
public class UUIDHelper {

    /**
     * @param uuid started time is 1970-01-01 00:00:00 UTC
     * need to shift 64 MSB bits to the right
     * to get the 48 bits containing
     * the Time-ordered UUID with Unix Epoch
     */
    public static LocalDateTime getTimestampFromUUIDv7(UUID uuid) {
        long millis = uuid.getMostSignificantBits() >>> 16;
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(millis),
                ZoneId.of("Asia/Ho_Chi_Minh")
        );
    }
}
