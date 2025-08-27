package fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.service;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.ETraceConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TraceConfigurationServiceImpl implements TraceConfigurationService {
  private final UuidGenerator uuidGenerator;
  private final ConfigurationRepository configurationRepository;

  @Override
  public TraceConfiguration getTraceConfiguration() {
    List<Configuration> configurations = configurationRepository.inScope(EConfigurationScope.TRACE);

    int maxDayRemaining =
        findTraceConfigurationMaxDay(configurations, ETraceConfiguration.MAX_REMINING_DAYS);

    int maxDayRemainingWarning =
        findTraceConfigurationMaxDay(
            configurations, ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_WARNING);

    int maxDayRemainingCritical =
        findTraceConfigurationMaxDay(
            configurations, ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_CRITICAL);

    return new TraceConfiguration(maxDayRemaining, maxDayRemainingWarning, maxDayRemainingCritical);
  }

  private int findTraceConfigurationMaxDay(
      List<Configuration> configurations, ETraceConfiguration traceConfiguration) {
    return Integer.parseInt(
        configurations.stream()
            .filter(c -> c.getKey() == traceConfiguration)
            .findAny()
            .orElseThrow()
            .getValue());
  }

  @Override
  public void postTraceConfiguration(TraceConfiguration traceConfiguration) {
    List<Configuration> configurations = configurationRepository.inScope(EConfigurationScope.TRACE);

    var maxRemainingDays =
        configurations.stream()
            .filter(c -> c.getKey() == ETraceConfiguration.MAX_REMINING_DAYS)
            .findAny()
            .orElse(
                Configuration.create(
                    uuidGenerator.generate(),
                    EConfigurationScope.TRACE,
                    ETraceConfiguration.MAX_REMINING_DAYS,
                    String.valueOf(traceConfiguration.maxRemainingDays())));

    var maxRemainingDaysBeforeWarning =
        configurations.stream()
            .filter(c -> c.getKey() == ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_WARNING)
            .findAny()
            .orElse(
                Configuration.create(
                    uuidGenerator.generate(),
                    EConfigurationScope.TRACE,
                    ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_WARNING,
                    String.valueOf(traceConfiguration.maxRemainingDaysBeforeWarning())));

    var maxRemainingDaysBeforeCritical =
        configurations.stream()
            .filter(c -> c.getKey() == ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_CRITICAL)
            .findAny()
            .orElse(
                Configuration.create(
                    uuidGenerator.generate(),
                    EConfigurationScope.TRACE,
                    ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_CRITICAL,
                    String.valueOf(traceConfiguration.maxRemainingDaysBeforeCritical())));

    maxRemainingDays.setValue(String.valueOf(traceConfiguration.maxRemainingDays()));
    maxRemainingDaysBeforeWarning.setValue(
        String.valueOf(traceConfiguration.maxRemainingDaysBeforeWarning()));
    maxRemainingDaysBeforeCritical.setValue(
        String.valueOf(traceConfiguration.maxRemainingDaysBeforeCritical()));

    configurationRepository.saveAll(
        List.of(maxRemainingDays, maxRemainingDaysBeforeWarning, maxRemainingDaysBeforeCritical));

    log.info("Trace configuration posted successfully: {}", traceConfiguration);
  }
}
