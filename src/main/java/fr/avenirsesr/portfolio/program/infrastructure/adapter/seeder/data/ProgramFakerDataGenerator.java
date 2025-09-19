package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.program.domain.port.output.seeder.ProgramDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class ProgramFakerDataGenerator extends DataGenerator implements ProgramDataGenerator {
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
  public String university() {
    return faker().university().name();
  }

  @Override
  public String program() {
    return "Program %s %s".formatted(faker().university().degree(), faker().university().suffix());
  }

  @Override
  public String skill() {
    return "Skill %s".formatted(faker().lorem().word());
  }

  @Override
  public String skillLevelName() {
    return "Skill level %s".formatted(faker().lorem().word());
  }

  @Override
  public String skillLevelDescription() {
    return "Skill level description %s".formatted(faker().lorem().sentence(10));
  }
}
