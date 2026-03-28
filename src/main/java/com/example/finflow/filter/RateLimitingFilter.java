package com.example.finflow.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final ProxyManager<String> proxyManager;

    private static final BucketConfiguration configuration = BucketConfiguration.builder()
            .addLimit(limit -> limit.capacity(20).refillGreedy(20, Duration.ofSeconds(1)))
            .build();

    public RateLimitingFilter(ProxyManager<String> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader("X-User-Id");
        String key = userId != null ? userId : request.getRemoteAddr();

        Bucket bucket = proxyManager.builder()
                .build(key, () -> configuration);

        boolean consumed = bucket.tryConsume(1);

        if(!consumed) {
            response.sendError(429, "Too many requests");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
