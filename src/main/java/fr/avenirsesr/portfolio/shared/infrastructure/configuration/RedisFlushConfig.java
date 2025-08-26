package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisFlushConfig {

  @Bean
  public ApplicationRunner flushRedisCache(RedisConnectionFactory redisConnectionFactory) {
    return args -> {
      try (RedisConnection connection = redisConnectionFactory.getConnection()) {
        connection.execute("FLUSHDB");
        System.out.println("Redis cache cleared on startup");
      } catch (Exception e) {
        System.err.println("Error while clearing the Redis cache : " + e.getMessage());
      }
    };
  }
}
