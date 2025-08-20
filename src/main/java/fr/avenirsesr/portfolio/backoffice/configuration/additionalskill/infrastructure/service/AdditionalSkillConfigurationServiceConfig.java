package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.infrastructure.service;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.port.input.AdditionalSkillConfigurationService;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.service.AdditionalSkillConfigurationServiceImpl;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AdditionalSkillConfigurationServiceConfig {
  private final ConfigurationRepository configurationRepository;

  public AdditionalSkillConfigurationServiceConfig(
      ConfigurationRepository configurationRepository) {
    this.configurationRepository = configurationRepository;
  }

  @Bean
  public AdditionalSkillConfigurationService additionalSkillConfigurationService() {
    return new AdditionalSkillConfigurationServiceImpl(configurationRepository);
  }
}
