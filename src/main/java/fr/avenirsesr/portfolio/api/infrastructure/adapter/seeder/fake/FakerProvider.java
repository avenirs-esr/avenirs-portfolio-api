package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake;

import java.util.Locale;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

@Slf4j
public class FakerProvider {
  private final boolean fixSeedEnabled = true;
  private static final long SEED = 100000L;
  private static final Locale LOCALE = Locale.FRENCH;
  private static long instanceCounter = 1;

  private final long seed;
  private long callCounter = 0;

  public FakerProvider() {
    this.seed = SEED * (instanceCounter++);
  }

  public Faker call() {
    if (fixSeedEnabled) {
      this.callCounter++;
      return new Faker(LOCALE, new Random(seed + callCounter));
    }

    return new Faker(LOCALE);
  }
}
