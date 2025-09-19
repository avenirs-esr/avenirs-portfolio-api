package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.ams.domain.port.output.seeder.AmsDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class AmsFakerDataGenerator extends DataGenerator implements AmsDataGenerator {
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
  public String title() {
    return "AMS %s".formatted(faker().lorem().word());
  }
}
