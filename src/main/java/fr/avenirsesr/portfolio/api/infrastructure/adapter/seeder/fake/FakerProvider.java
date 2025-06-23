package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake;

import java.util.Locale;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

@Slf4j
public class FakerProvider {
  private final boolean fixSeedEnabled = true;
  private static final long DEFAULT_SEED = 100000L;
  private static final Locale LOCALE = Locale.FRENCH;
  private static Faker FIXED_SEED_FAKER;
  private final long seed;

  public FakerProvider() {
    this(DEFAULT_SEED);
  }

  public FakerProvider(long seed) {
    this.seed = seed;
  }

  public Faker call() {
    if (fixSeedEnabled) {
      if (FIXED_SEED_FAKER == null) {
        FIXED_SEED_FAKER = new Faker(LOCALE, new Random(seed));
      }
      return FIXED_SEED_FAKER;
    }

    return new Faker(LOCALE);
  }
}
