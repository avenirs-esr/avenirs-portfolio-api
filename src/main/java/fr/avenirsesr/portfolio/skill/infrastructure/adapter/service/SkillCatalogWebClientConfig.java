package fr.avenirsesr.portfolio.skill.infrastructure.adapter.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SkillCatalogWebClientConfig {
  private static final int MAX_CATALOG_SIZE_IN_BYTES = 64 * 1024 * 1024;

  @Bean
  public WebClient skillCatalogWebClient() {
    return WebClient.builder()
        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_CATALOG_SIZE_IN_BYTES))
        .build();
  }
}
