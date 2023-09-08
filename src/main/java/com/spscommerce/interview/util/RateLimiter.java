package com.spscommerce.interview.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimiter {

    @Value("${rate-limits.requests}")
    private int requests;
    @Value("${rate-limits.duration}")
    private int duration;
    @Value("${rate-limits.timeUnit}")
    private TimeUnit timeUnit;

    private Map<String, Bucket> clientBuckets = new HashMap<>();

    public Bucket getBucket() {
        Jwt principal = (Jwt)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sub = principal.getClaim("sub");
        if (!clientBuckets.containsKey(sub)) {
            clientBuckets.put(sub, buildBucket());
        }
        return clientBuckets.get(sub);
    }

    private Bucket buildBucket() {
        return Bucket.builder().addLimit(Bandwidth.classic(requests, getRefill())).build();
    }

    private Refill getRefill() {
        switch (timeUnit) {
            case SECOND -> {
                return Refill.intervally(requests, Duration.ofSeconds(duration));
            }
            case MINUTE -> {
                return Refill.intervally(requests, Duration.ofMinutes(duration));
            }
            case HOUR -> {
                return Refill.intervally(requests, Duration.ofHours(duration));
            }
            case DAY -> {
                return Refill.intervally(requests, Duration.ofDays(duration));
            }
        }
        return Refill.intervally(requests, Duration.ofSeconds(duration));
    }


    enum TimeUnit {
        SECOND,
        MINUTE,
        HOUR,
        DAY
    }



}
