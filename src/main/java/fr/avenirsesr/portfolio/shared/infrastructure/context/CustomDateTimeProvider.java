package fr.avenirsesr.portfolio.shared.infrastructure.context;

import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

@Component("customDateTimeProvider")
public class CustomDateTimeProvider implements DateTimeProvider {

  private final ClockService clockService;

  public CustomDateTimeProvider(ClockService clockService) {
    this.clockService = clockService;
  }

  @Override
  public Optional<TemporalAccessor> getNow() {
    return Optional.of(clockService.now());
  }
}
