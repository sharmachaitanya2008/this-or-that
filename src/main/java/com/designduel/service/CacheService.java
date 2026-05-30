package com.designduel.service;

import com.designduel.repository.DesignRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j

public class CacheService {

    private static final Duration CACHE_TTL = Duration.ofSeconds(30);

    private final DesignRepository designRepository;

    private volatile long cachedActiveDesignCount = 0;
    private volatile Instant lastRefresh = Instant.EPOCH;

    public CacheService(DesignRepository designRepository) {
        this.designRepository = designRepository;
    }

    public long getActiveDesignCount() {
        if (Duration.between(lastRefresh, Instant.now()).compareTo(CACHE_TTL) > 0) {
            refreshActiveDesignCount();
        }
        return cachedActiveDesignCount;
    }

    public void refreshActiveDesignCount() {
        cachedActiveDesignCount = designRepository.countByActiveTrue();
        lastRefresh = Instant.now();
        log.debug("Active design count refreshed: {}", cachedActiveDesignCount);
    }

    public void invalidateActiveDesignCount() {
        lastRefresh = Instant.EPOCH;
    }
}
