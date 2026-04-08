package by.pcconf.pcconfigurer.config;

import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {
  @Override
  public void customize(ConcurrentMapCacheManager cacheManager) {
    cacheManager.setCacheNames(List.of("products"));
  }
}
