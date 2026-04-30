package fr.avenirsesr.portfolio.shared.domain.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClockServiceImpl implements ClockService {
  private Clock clock = Clock.systemDefaultZone();

  @Override
  public Clock getClock() {
    return clock;
  }

  @Override
  public void set(Clock clock) {
    this.clock = clock;
  }

  @Override
  public void fixed(Instant instant) {
    this.clock = Clock.fixed(instant, ZoneId.systemDefault());
  }

  @Override
  public void clear() {
    this.clock = Clock.systemDefaultZone();
  }

  @Override
  public Instant now() {
    return Instant.now(clock);
  }
}
