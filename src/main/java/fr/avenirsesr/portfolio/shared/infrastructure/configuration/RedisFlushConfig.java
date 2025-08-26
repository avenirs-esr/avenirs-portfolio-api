package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Slf4j
@Configuration
public class RedisFlushConfig {

  @Bean
  public ApplicationRunner flushRedisCache(RedisConnectionFactory redisConnectionFactory) {
    return args -> {
      try (RedisConnection connection = redisConnectionFactory.getConnection()) {
        connection.execute("FLUSHDB");
        log.info("Redis cache cleared on startup");
      } catch (Exception e) {
        log.error("Error while clearing the Redis cache : {}", e.getMessage());
      }
    };
  }
}
