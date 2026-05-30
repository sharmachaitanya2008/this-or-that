package com.designduel.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final boolean enabled;
    private final long capacity;
    private final long refillPerMinute;

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public RateLimitFilter(
            @Value("${app.rate-limit.enabled:false}") boolean enabled,
            @Value("${app.rate-limit.capacity:30}") long capacity,
            @Value("${app.rate-limit.refill-per-minute:20}") long refillPerMinute) {
        this.enabled = enabled;
        this.capacity = capacity;
        this.refillPerMinute = refillPerMinute;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.equals("/")
                || path.endsWith(".html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!enabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = resolveKey(request);
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(capacity, refillPerMinute));

        if (bucket.tryConsume()) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for key: {} path: {}", key, request.getRequestURI());
            response.setContentType("application/json");
            response.setStatus(429);
            response.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Please slow down.\"}");
        }
    }

    private String resolveKey(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        String ip = request.getRemoteAddr();
        return ip != null ? ip : "unknown";
    }

    static class TokenBucket {
        private final long capacity;
        private final long refillIntervalMs;
        private final AtomicLong tokens;
        private volatile long lastRefill;

        TokenBucket(long capacity, long refillPerMinute) {
            this.capacity = capacity;
            this.refillIntervalMs = 60_000 / refillPerMinute;
            this.tokens = new AtomicLong(capacity);
            this.lastRefill = System.currentTimeMillis();
        }

        boolean tryConsume() {
            refill();
            long current;
            do {
                current = tokens.get();
                if (current <= 0) return false;
            } while (!tokens.compareAndSet(current, current - 1));
            return true;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefill;
            if (elapsed < refillIntervalMs) return;

            long refillCount = elapsed / refillIntervalMs;
            if (refillCount > 0) {
                lastRefill = now;
                tokens.updateAndGet(t -> Math.min(capacity, t + refillCount));
            }
        }
    }
}
