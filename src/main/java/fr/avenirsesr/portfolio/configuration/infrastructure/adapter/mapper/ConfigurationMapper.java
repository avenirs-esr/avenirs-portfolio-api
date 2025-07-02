package fr.avenirsesr.portfolio.configuration.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.configuration.domain.model.Configuration;
import fr.avenirsesr.portfolio.configuration.infrastructure.adapter.model.ConfigurationEntity;

public interface ConfigurationMapper {
  static ConfigurationEntity fromDomain(Configuration configuration) {
    return new ConfigurationEntity(
        configuration.getId(), configuration.getName(), configuration.getValue());
  }

  static Configuration toDomain(ConfigurationEntity configurationEntity) {
    return Configuration.toDomain(
        configurationEntity.getId(), configurationEntity.getName(), configurationEntity.getValue());
  }
}
