package hu.blog.megosztanam.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CachingConfig {

    private final int minutes;
    private final int size;

    public CachingConfig(@Value("${lol.cache.minutes}") int minutes,
                         @Value("${lol.cache.size}") int size) {
        this.minutes = minutes;
        this.size = size;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder()
                        .expireAfterWrite(minutes, TimeUnit.MINUTES)
                        .maximumSize(size)
                        .build()
                        .asMap(), false);
            }
        };
    }
}