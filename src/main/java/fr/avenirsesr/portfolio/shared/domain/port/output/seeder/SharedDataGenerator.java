package fr.avenirsesr.portfolio.shared.domain.port.output.seeder;

import java.util.List;
import java.util.UUID;

public interface SharedDataGenerator extends DataGeneratorInterface {
  UUID uuid();

  String externalId();

  <T> T pickIn(List<T> list);

  <E extends Enum<E>> E pickIn(Class<E> enumClass);

  int number();

  int number(int max);

  int number(int min, int max);

  boolean bool();

  String regexify(String regex);
}
