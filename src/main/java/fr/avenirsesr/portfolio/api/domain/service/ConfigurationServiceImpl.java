package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.TraceConfigurationInfo;
import fr.avenirsesr.portfolio.api.domain.model.enums.EConfiguration;
import fr.avenirsesr.portfolio.api.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ConfigurationEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
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
