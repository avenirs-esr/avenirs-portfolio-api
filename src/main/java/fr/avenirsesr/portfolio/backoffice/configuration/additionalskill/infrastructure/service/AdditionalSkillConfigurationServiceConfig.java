package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.infrastructure.service;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.port.input.AdditionalSkillConfigurationService;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.service.AdditionalSkillConfigurationServiceImpl;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.input.service.ConfigurationTranslationService;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AdditionalSkillConfigurationServiceConfig {
  private final UuidGenerator uuidGenerator;
  private final ConfigurationRepository configurationRepository;
  private final ConfigurationTranslationService configurationTranslationService;

  public AdditionalSkillConfigurationServiceConfig(
      UuidGenerator uuidGenerator,
      ConfigurationRepository configurationRepository,
      ConfigurationTranslationService configurationTranslationService) {
    this.uuidGenerator = uuidGenerator;
    this.configurationRepository = configurationRepository;
    this.configurationTranslationService = configurationTranslationService;
  }

  @Bean
  public AdditionalSkillConfigurationService additionalSkillConfigurationService() {
    return new AdditionalSkillConfigurationServiceImpl(
        uuidGenerator, configurationRepository, configurationTranslationService);
  }
}
