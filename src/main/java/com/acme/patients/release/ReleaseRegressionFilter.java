package com.acme.patients.release;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates a bad release for the canary demo. Off by default; the "bad" build
 * sets acme.release.regression=true so a share of API calls fail and slow down.
 */
@Component
public class ReleaseRegressionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ReleaseRegressionFilter.class);

    private final boolean enabled;
    private final double errorRate;
    private final long latencyMs;

    public ReleaseRegressionFilter(@Value("${acme.release.regression:false}") boolean enabled,
                                   @Value("${acme.release.error-rate:0.4}") double errorRate,
                                   @Value("${acme.release.latency-ms:300}") long latencyMs) {
        this.enabled = enabled;
        this.errorRate = errorRate;
        this.latencyMs = latencyMs;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !enabled || !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (ThreadLocalRandom.current().nextDouble() < errorRate) {
            log.error("Unhandled exception serving {}", request.getRequestURI(),
                    new IllegalStateException("record cache returned null for " + request.getRequestURI()));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        chain.doFilter(request, response);
    }
}
