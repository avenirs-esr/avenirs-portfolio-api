package fr.avenirsesr.portfolio.shared.domain.port.input;

import java.time.Clock;
import java.time.Instant;

public interface ClockService {
  Clock getClock();

  void set(Clock clock);

  void fixed(Instant instant);

  void clear();

  Instant now();
}
