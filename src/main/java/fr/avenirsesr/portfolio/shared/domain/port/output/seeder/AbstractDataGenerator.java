package fr.avenirsesr.portfolio.shared.domain.port.output.seeder;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractDataGenerator implements DataGeneratorInterface {
  private Random random;
  private ELanguage language;
}
