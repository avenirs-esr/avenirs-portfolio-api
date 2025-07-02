package fr.avenirsesr.portfolio.configuration.domain.port.input;

import fr.avenirsesr.portfolio.configuration.domain.model.TraceConfigurationInfo;

public interface ConfigurationService {
  TraceConfigurationInfo getTraceConfiguration();
}
