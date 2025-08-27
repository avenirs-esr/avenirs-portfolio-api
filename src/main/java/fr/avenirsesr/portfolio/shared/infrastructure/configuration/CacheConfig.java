package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillPagedResult;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.utils.AdditionalSkillConstants;
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
  public CacheManager additionalSkillCacheManager(RedisConnectionFactory factory) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    Jackson2JsonRedisSerializer<AdditionalSkillPagedResult> serializer =
        new Jackson2JsonRedisSerializer<>(mapper, AdditionalSkillPagedResult.class);

    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(serializer));

    return RedisCacheManager.builder(factory)
        .withCacheConfiguration(AdditionalSkillConstants.INDEX, config)
        .build();
  }
}
