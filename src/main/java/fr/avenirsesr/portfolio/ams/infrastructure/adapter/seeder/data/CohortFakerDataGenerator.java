package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.CohortDataGenerator;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.AbstractDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class CohortFakerDataGenerator extends AbstractDataGenerator implements CohortDataGenerator {
  private Faker faker() {
    return new Faker(
        switch (getLanguage()) {
          case ENGLISH -> Locale.ENGLISH;
          case FRENCH -> Locale.FRENCH;
          case SPANISH -> new Locale("es");
        },
        getRandom());
  }

  @Override
  public String name() {
    return "Cohort %s".formatted(faker().lorem().word());
  }

  @Override
  public String description() {
    return "Cohort description %s".formatted(faker().lorem().sentence(10));
  }
}
