package fr.avenirsesr.portfolio.shared.domain.port.output.seeder;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.Random;

public interface DataGeneratorInterface {
  Random getRandom();

  void setRandom(Random random);

  ELanguage getLanguage();

  void setLanguage(ELanguage language);
}
