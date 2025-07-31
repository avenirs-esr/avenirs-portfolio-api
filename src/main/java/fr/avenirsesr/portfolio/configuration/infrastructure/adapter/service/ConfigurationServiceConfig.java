package fr.avenirsesr.portfolio.configuration.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.configuration.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.configuration.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.configuration.domain.service.ConfigurationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ConfigurationServiceConfig {

  private final ConfigurationRepository configurationRepository;

  public ConfigurationServiceConfig(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  @Bean
  public ConfigurationService configurationService() {
    return new ConfigurationServiceImpl(configurationRepository);
  }
}
