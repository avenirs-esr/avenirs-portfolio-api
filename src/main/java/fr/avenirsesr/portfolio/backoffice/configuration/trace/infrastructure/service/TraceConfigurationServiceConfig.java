package fr.avenirsesr.portfolio.backoffice.configuration.trace.infrastructure.service;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.service.TraceConfigurationServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TraceConfigurationServiceConfig {

  private final UuidGenerator uuidGenerator;
  private final ConfigurationRepository configurationRepository;

  public TraceConfigurationServiceConfig(
      UuidGenerator uuidGenerator, ConfigurationRepository configurationRepository) {
    this.uuidGenerator = uuidGenerator;
    this.configurationRepository = configurationRepository;
  }

  @Bean
  public TraceConfigurationService traceConfigurationService() {
    return new TraceConfigurationServiceImpl(uuidGenerator, configurationRepository);
  }
}
