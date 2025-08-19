package fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.model.ConfigurationEntity;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.ETraceConfiguration;

public interface ConfigurationMapper {
  static ConfigurationEntity fromDomain(Configuration configuration) {
    return ConfigurationEntity.of(
        configuration.getId(),
        configuration.getScope(),
        configuration.getKey(),
        configuration.getValue());
  }

  static Configuration toDomain(ConfigurationEntity configurationEntity) {
    return Configuration.toDomain(
        configurationEntity.getId(),
        configurationEntity.getScope(),
        switch (configurationEntity.getScope()) {
          case TRACE -> ETraceConfiguration.valueOf(configurationEntity.getKey());
        },
        configurationEntity.getValue());
  }
}
