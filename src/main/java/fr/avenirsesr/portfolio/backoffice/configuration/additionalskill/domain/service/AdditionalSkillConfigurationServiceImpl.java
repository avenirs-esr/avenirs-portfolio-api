package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.service;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillLevel;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.EAdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.port.input.AdditionalSkillConfigurationService;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AdditionalSkillConfigurationServiceImpl
    implements AdditionalSkillConfigurationService {
  private final ConfigurationRepository configurationRepository;

  private String getAdditionalSkillLevelConfigFrom(
      List<Configuration> configurations, EAdditionalSkillConfiguration key) {
    return configurations.stream()
        .filter(c -> c.getKey() == key)
        .findFirst()
        .orElseThrow()
        .getValue();
  }

  @Override
  public AdditionalSkillConfiguration getConfiguration() {
    List<Configuration> configurations =
        configurationRepository.inScope(EConfigurationScope.ADDITIONAL_SKILL);

    return new AdditionalSkillConfiguration(
        new AdditionalSkillLevel(
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_BEGINNER_LABEL),
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_BEGINNER_DESCRIPTION)),
        new AdditionalSkillLevel(
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_INTERMEDIATE_LABEL),
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_INTERMEDIATE_DESCRIPTION)),
        new AdditionalSkillLevel(
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_COMPETENT_LABEL),
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_COMPETENT_DESCRIPTION)),
        new AdditionalSkillLevel(
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_ADVANCED_LABEL),
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_ADVANCED_DESCRIPTION)),
        new AdditionalSkillLevel(
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_EXPERT_LABEL),
            getAdditionalSkillLevelConfigFrom(
                configurations, EAdditionalSkillConfiguration.LEVEL_EXPERT_DESCRIPTION)));
  }

  @Override
  public void postConfiguration(AdditionalSkillConfiguration configuration) {
    List<Configuration> configurations =
        configurationRepository.inScope(EConfigurationScope.ADDITIONAL_SKILL);

    var newConfigurations = new ArrayList<Configuration>();

    for (EAdditionalSkillConfiguration key : EAdditionalSkillConfiguration.values()) {
      var value =
          switch (key) {
            case LEVEL_BEGINNER_LABEL -> configuration.BEGINNER().label();
            case LEVEL_BEGINNER_DESCRIPTION -> configuration.BEGINNER().description();
            case LEVEL_INTERMEDIATE_LABEL -> configuration.INTERMEDIATE().label();
            case LEVEL_INTERMEDIATE_DESCRIPTION -> configuration.INTERMEDIATE().description();
            case LEVEL_COMPETENT_LABEL -> configuration.COMPETENT().label();
            case LEVEL_COMPETENT_DESCRIPTION -> configuration.COMPETENT().description();
            case LEVEL_ADVANCED_LABEL -> configuration.ADVANCED().label();
            case LEVEL_ADVANCED_DESCRIPTION -> configuration.ADVANCED().description();
            case LEVEL_EXPERT_LABEL -> configuration.EXPERT().label();
            case LEVEL_EXPERT_DESCRIPTION -> configuration.EXPERT().description();
          };

      var newConfiguration =
          configurations.stream()
              .filter(c -> c.getKey() == key)
              .findAny()
              .orElse(
                  Configuration.create(
                      UUID.randomUUID(), EConfigurationScope.ADDITIONAL_SKILL, key, value));

      newConfiguration.setValue(value);

      newConfigurations.add(newConfiguration);
    }

    configurationRepository.saveAll(newConfigurations);

    log.info("Added additional skill configurations : {}", newConfigurations);
  }
}
