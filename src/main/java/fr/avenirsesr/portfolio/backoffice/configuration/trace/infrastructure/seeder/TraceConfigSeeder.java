package fr.avenirsesr.portfolio.backoffice.configuration.trace.infrastructure.seeder;

import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceConfigSeeder {
  private final TraceConfigurationService traceConfigurationService;

  public void seed() {
    log.info("Seeding trace configuration...");
    var config = new TraceConfiguration(90, 10, 5);

    traceConfigurationService.postTraceConfiguration(config);

    log.info("✔ traces configuration saved : {}", config);
  }
}
