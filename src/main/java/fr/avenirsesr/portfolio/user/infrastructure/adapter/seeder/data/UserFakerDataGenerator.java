package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class UserFakerDataGenerator extends DataGenerator implements UserDataGenerator {
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
  public String firstName() {
    return faker().name().firstName();
  }

  @Override
  public String lastName() {
    return faker().name().lastName();
  }

  @Override
  public String email() {
    return faker().internet().emailAddress();
  }

  @Override
  public String studentDescription() {
    return faker().lorem().sentence(10);
  }

  @Override
  public String staffDescription() {
    return faker().lorem().sentence(10);
  }
}
