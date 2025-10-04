package com.vuong.vmess.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;

import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitRedisConfig {
    @Bean
    public RedisClient redisClient(RedisProperties props) {
        return RedisClient.create(
                RedisURI.builder()
                        .withHost(props.getHost())
                        .withPort(props.getPort())
                        .withSsl(false)
                        .withDatabase(props.getDatabase())
                        .build()
        );
    }

    @Bean
    public ProxyManager<String> bucketProxyManager(RedisClient redisClient) {
        StatefulRedisConnection<String, byte[]> conn =
                redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        return LettuceBasedProxyManager.builderFor(conn)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(1))
                )
                .build();
    }

}
