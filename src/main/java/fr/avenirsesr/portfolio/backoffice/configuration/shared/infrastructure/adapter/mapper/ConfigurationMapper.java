package fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.EAdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.model.ConfigurationEntity;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.ETraceConfiguration;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.TranslationUtil;

public interface ConfigurationMapper {
  static ConfigurationEntity fromDomain(Configuration configuration) {
    return ConfigurationEntity.of(
        configuration.getId(),
        configuration.getScope(),
        configuration.getKey(),
        configuration.getValue());
  }

  static ConfigurationEntity fromDomainWithoutValue(Configuration configuration) {
    return ConfigurationEntity.of(
        configuration.getId(), configuration.getScope(), configuration.getKey(), null);
  }

  static Configuration toDomain(ConfigurationEntity configurationEntity) {
    return Configuration.toDomain(
        configurationEntity.getId(),
        configurationEntity.getScope(),
        switch (configurationEntity.getScope()) {
          case TRACE -> ETraceConfiguration.valueOf(configurationEntity.getKey());
          case ADDITIONAL_SKILL ->
              EAdditionalSkillConfiguration.valueOf(configurationEntity.getKey());
        },
        configurationEntity.getValue().isPresent()
            ? configurationEntity.getValue().get()
            : TranslationUtil.getTranslation(configurationEntity.getTranslations()).getValue(),
        configurationEntity.getCreatedAt(),
        configurationEntity.getUpdatedAt());
  }
}
