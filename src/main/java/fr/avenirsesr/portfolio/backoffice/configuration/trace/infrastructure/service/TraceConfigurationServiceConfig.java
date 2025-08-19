package fr.avenirsesr.portfolio.backoffice.configuration.trace.infrastructure.service;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.service.TraceConfigurationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TraceConfigurationServiceConfig {

  private final ConfigurationRepository configurationRepository;

  public TraceConfigurationServiceConfig(ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  @Bean
  public TraceConfigurationService configurationService() {
    return new TraceConfigurationServiceImpl(configurationRepository);
  }
}
