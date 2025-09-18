package fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;

public interface ConfigurationRepository extends GenericRepositoryPort<Configuration> {
  List<Configuration> inScope(EConfigurationScope scope);
}
