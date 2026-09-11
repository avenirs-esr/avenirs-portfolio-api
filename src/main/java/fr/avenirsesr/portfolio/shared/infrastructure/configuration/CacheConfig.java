package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.group.application.adapter.dto.GroupDTO;
import fr.avenirsesr.portfolio.common.institution.application.adapter.dto.InstitutionDTO;
import java.time.Duration;
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
  public CacheManager externalSkillCacheManager(RedisConnectionFactory factory) {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @SuppressWarnings("unchecked")
    Jackson2JsonRedisSerializer<PagedResponse<ExternalSkillDTO>> searchSerializer =
        (Jackson2JsonRedisSerializer<PagedResponse<ExternalSkillDTO>>)
            (Jackson2JsonRedisSerializer<?>)
                new Jackson2JsonRedisSerializer<>(mapper, PagedResponse.class);

    Jackson2JsonRedisSerializer<ExternalSkillDetailsDTO> detailsSerializer =
        new Jackson2JsonRedisSerializer<>(mapper, ExternalSkillDetailsDTO.class);

    Jackson2JsonRedisSerializer<ExternalSkillDTO> byIdSerializer =
        new Jackson2JsonRedisSerializer<>(mapper, ExternalSkillDTO.class);

    Jackson2JsonRedisSerializer<InstitutionDTO> institutionSerializer =
        new Jackson2JsonRedisSerializer<>(mapper, InstitutionDTO.class);

    Jackson2JsonRedisSerializer<GroupDTO> groupSerializer =
        new Jackson2JsonRedisSerializer<>(mapper, GroupDTO.class);

    RedisCacheConfiguration searchConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(searchSerializer));

    RedisCacheConfiguration detailsConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(24))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(detailsSerializer));

    RedisCacheConfiguration byIdConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(24))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(byIdSerializer));

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

    return RedisCacheManager.builder(factory)
        .withCacheConfiguration("external-skill-search", searchConfig)
        .withCacheConfiguration("externalSkillDetails", detailsConfig)
        .withCacheConfiguration("externalSkillById", byIdConfig)
        .withCacheConfiguration("institutionById", institutionConfig)
        .withCacheConfiguration("groupById", groupConfig)
        .withCacheConfiguration("programByGroupId", groupConfig)
        .build();
  }
}
