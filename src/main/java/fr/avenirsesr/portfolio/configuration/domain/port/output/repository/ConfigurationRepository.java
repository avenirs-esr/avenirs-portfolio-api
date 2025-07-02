package fr.avenirsesr.portfolio.configuration.domain.port.output.repository;

import fr.avenirsesr.portfolio.configuration.domain.model.Configuration;
import fr.avenirsesr.portfolio.configuration.infrastructure.adapter.model.ConfigurationEntity;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;

public interface ConfigurationRepository extends GenericRepositoryPort<Configuration> {
  List<ConfigurationEntity> getTraceConfiguration();
}
