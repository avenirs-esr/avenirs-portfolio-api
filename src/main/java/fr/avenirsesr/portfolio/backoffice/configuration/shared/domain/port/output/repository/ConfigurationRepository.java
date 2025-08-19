package fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.model.ConfigurationEntity;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;

public interface ConfigurationRepository extends GenericRepositoryPort<Configuration> {
  List<ConfigurationEntity> inScope(EConfigurationScope scope);
}
