package fr.avenirsesr.portfolio.configuration.domain.service;

import fr.avenirsesr.portfolio.configuration.domain.model.TraceConfigurationInfo;
import fr.avenirsesr.portfolio.configuration.domain.model.enums.EConfiguration;
import fr.avenirsesr.portfolio.configuration.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.configuration.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.configuration.infrastructure.adapter.model.ConfigurationEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {
  private final ConfigurationRepository configurationRepository;

  @Override
  public TraceConfigurationInfo getTraceConfiguration() {
    List<ConfigurationEntity> configurationEntities =
        configurationRepository.getTraceConfiguration();

    int maxDayRemaining = 0, maxDayRemainingWarning = 0, maxDayRemainingCritical = 0;

    for (ConfigurationEntity configurationEntity : configurationEntities) {
      switch (EConfiguration.valueOf(configurationEntity.getName())) {
        case EConfiguration.TRACE_MAX_DAY_REMAINING_NORMAL:
          maxDayRemaining = Integer.parseInt(configurationEntity.getValue());
          break;
        case EConfiguration.TRACE_MAX_DAY_REMAINING_WARNING:
          maxDayRemainingWarning = Integer.parseInt(configurationEntity.getValue());
          break;
        case EConfiguration.TRACE_MAX_DAY_REMAINING_CRITICAL:
          maxDayRemainingCritical = Integer.parseInt(configurationEntity.getValue());
          break;
      }
    }

    return new TraceConfigurationInfo(
        maxDayRemaining, maxDayRemainingWarning, maxDayRemainingCritical);
  }
}
