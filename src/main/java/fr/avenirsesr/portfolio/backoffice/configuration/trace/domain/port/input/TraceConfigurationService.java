package fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input;

import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.TraceConfiguration;

public interface TraceConfigurationService {
  TraceConfiguration getTraceConfiguration();

  void postTraceConfiguration(TraceConfiguration traceConfiguration);
}
