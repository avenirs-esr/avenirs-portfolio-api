package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.StaffDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class StaffFakerDataGenerator extends DataGenerator implements StaffDataGenerator {
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
  public String staffDescription() {
    return faker().lorem().sentence(10);
  }
}
