package fr.avenirsesr.portfolio.trace.domain.port.output;

import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;

public interface TraceConfigurationPort {
  TraceConfiguration getTraceConfiguration();
}
