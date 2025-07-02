package fr.avenirsesr.portfolio.configuration.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.configuration.domain.model.Configuration;
import fr.avenirsesr.portfolio.configuration.domain.model.enums.EConfiguration;
import fr.avenirsesr.portfolio.configuration.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.configuration.infrastructure.adapter.mapper.ConfigurationMapper;
import fr.avenirsesr.portfolio.configuration.infrastructure.adapter.model.ConfigurationEntity;
import fr.avenirsesr.portfolio.configuration.infrastructure.adapter.specification.ConfigurationSpecification;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationDatabaseRepository
    extends GenericJpaRepositoryAdapter<Configuration, ConfigurationEntity>
    implements ConfigurationRepository {
  public ConfigurationDatabaseRepository(ConfigurationJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        ConfigurationMapper::fromDomain,
        ConfigurationMapper::toDomain);
  }

  @Override
  public List<ConfigurationEntity> getTraceConfiguration() {
    return jpaSpecificationExecutor.findAll(
        ConfigurationSpecification.byAnyName(Arrays.stream(EConfiguration.values()).toList()));
  }
}
