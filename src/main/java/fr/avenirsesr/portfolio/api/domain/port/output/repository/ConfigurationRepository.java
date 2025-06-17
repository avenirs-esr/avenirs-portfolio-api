package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Configuration;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ConfigurationEntity;
import java.util.List;

public interface ConfigurationRepository extends GenericRepositoryPort<Configuration> {
  List<ConfigurationEntity> getTraceConfiguration();
}
