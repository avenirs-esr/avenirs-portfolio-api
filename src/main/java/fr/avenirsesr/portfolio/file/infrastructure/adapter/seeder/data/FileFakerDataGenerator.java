package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.output.seeder.FileDataGenerator;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.AbstractDataGenerator;
import java.util.Locale;
import net.datafaker.Faker;

public class FileFakerDataGenerator extends AbstractDataGenerator implements FileDataGenerator {
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
  public String fileName(EFileType fileType) {
    return "%s.%s".formatted(faker().lorem().word(), fileType.name().toLowerCase());
  }
}
