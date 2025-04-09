package org.app.assignment.productrest.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCache allProducts = new CaffeineCache("allProducts",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(45))
                        .build());

        CaffeineCache productByCode = new CaffeineCache("productByCode",
                Caffeine.newBuilder()
                        .maximumSize(50)
                        .expireAfterWrite(Duration.ofMinutes(50))
                        .build());

        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(Arrays.asList(
                allProducts,
                productByCode
        ));

        simpleCacheManager.initializeCaches();

        return simpleCacheManager;
    }
}
