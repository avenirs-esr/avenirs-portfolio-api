package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.common.institution.application.adapter.dto.InstitutionDTO;
import java.time.Duration;
import java.util.UUID;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager avenirsCacheManager(RedisConnectionFactory factory) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    Jackson2JsonRedisSerializer<InstitutionDTO> institutionSerializer =
        new Jackson2JsonRedisSerializer<>(mapper, InstitutionDTO.class);

    Jackson2JsonRedisSerializer<GroupDTO> groupSerializer =
        new Jackson2JsonRedisSerializer<>(mapper, GroupDTO.class);

    Jackson2JsonRedisSerializer<UUID> latestCguIdSerializer =
        new Jackson2JsonRedisSerializer<>(mapper, UUID.class);

    RedisCacheConfiguration institutionConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(24))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(institutionSerializer));

    RedisCacheConfiguration groupConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(24))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(groupSerializer));

    RedisCacheConfiguration latestCguIdConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(latestCguIdSerializer));

    return RedisCacheManager.builder(factory)
        .withCacheConfiguration("institutionById", institutionConfig)
        .withCacheConfiguration("groupById", groupConfig)
        .withCacheConfiguration("programByGroupId", groupConfig)
        .withCacheConfiguration("latestCguId", latestCguIdConfig)
        .build();
  }
}
