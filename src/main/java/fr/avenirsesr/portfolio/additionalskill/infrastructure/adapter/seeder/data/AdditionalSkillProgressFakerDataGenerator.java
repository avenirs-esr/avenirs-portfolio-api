package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.seeder.AdditionalSkillProgressDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class AdditionalSkillProgressFakerDataGenerator extends DataGenerator
    implements AdditionalSkillProgressDataGenerator {
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
  public String description() {
    return faker().lorem().sentence(10);
  }
}
