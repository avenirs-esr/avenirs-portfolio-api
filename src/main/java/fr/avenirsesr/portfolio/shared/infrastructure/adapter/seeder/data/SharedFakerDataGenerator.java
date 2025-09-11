package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.AbstractDataGenerator;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.SharedDataGenerator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@RequiredArgsConstructor
public class SharedFakerDataGenerator extends AbstractDataGenerator implements SharedDataGenerator {
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
  public UUID uuid() {
    return UUID.fromString(faker().internet().uuid());
  }

  @Override
  public String externalId() {
    int externalIdType = faker().number().numberBetween(0, 3);
    return switch (externalIdType) {
      case 0 -> faker().internet().uuid();
      case 1 -> String.valueOf(faker().number().numberBetween(1, 999_999));
      case 2 -> faker().regexify("[A-Z]{3}[0-9]{3}");
      default -> throw new IllegalStateException("Unexpected value: " + externalIdType);
    };
  }

  @Override
  public <T> T pickIn(List<T> list) {
    return faker().options().nextElement(list);
  }

  @Override
  public <E extends Enum<E>> E pickIn(Class<E> enumClass) {
    return faker().options().option(enumClass);
  }

  @Override
  public int number() {
    return faker().random().nextInt();
  }

  @Override
  public int number(int max) {
    return faker().random().nextInt(max);
  }

  @Override
  public int number(int min, int max) {
    return faker().random().nextInt(min, max);
  }

  @Override
  public boolean bool() {
    return faker().random().nextBoolean();
  }

  @Override
  public String regexify(String regex) {
    return faker().regexify(regex);
  }
}
