package by.pcconf.pcconfigurer.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class ProductCacheEvict {

  @CacheEvict(value = "products", allEntries = true)
  @Scheduled(fixedRateString = "${caching.spring.productsTTL}")
  public void emptyProductsCache() {
    log.info("Emptying products cache");
  }
}
