package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGenerator;
import fr.avenirsesr.portfolio.student.trace.domain.port.output.seeder.TraceDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class TraceFakerDataGenerator extends DataGenerator implements TraceDataGenerator {
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
  public String traceName() {
    return "Trace %s".formatted(faker().lorem().word());
  }

  @Override
  public String traceAiJustification() {
    return "I use AI for : %s".formatted(faker().lorem().sentence(5));
  }

  @Override
  public String tracePersonalNote() {
    return "Personal note : %s".formatted(faker().lorem().sentence(5));
  }

  @Override
  public String traceLink() {
    return faker().internet().url();
  }
}
