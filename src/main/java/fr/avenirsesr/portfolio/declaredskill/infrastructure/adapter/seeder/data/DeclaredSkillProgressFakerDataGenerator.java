package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.seeder.DeclaredSkillProgressDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class DeclaredSkillProgressFakerDataGenerator extends DataGenerator
    implements DeclaredSkillProgressDataGenerator {
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
