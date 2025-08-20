package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;

@Slf4j
public class FakerProvider {
  private static final Locale LOCALE = Locale.FRENCH;
  private static final HashMap<String, Integer> seedCounts = new HashMap<>();
  private String globalSeed;

  public FakerProvider init(Class<?> clazz) {
    globalSeed = clazz.getSimpleName();
    return this;
  }

  public Faker call(String seed) {
    var key = String.join(globalSeed, "-", seed);
    int count = Optional.ofNullable(seedCounts.get(key)).orElse(0);
    count += 1;
    seedCounts.put(key, count);
    return new Faker(LOCALE, new Random((long) key.hashCode() * count));
  }
}
