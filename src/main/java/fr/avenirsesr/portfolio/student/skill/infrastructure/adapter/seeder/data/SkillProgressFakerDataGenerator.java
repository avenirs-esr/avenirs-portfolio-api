package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.seeder.SkillProgressDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class SkillProgressFakerDataGenerator extends DataGenerator
    implements SkillProgressDataGenerator {
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
  public String reflection() {
    return faker().lorem().sentence(10);
  }
}
