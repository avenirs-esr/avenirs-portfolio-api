package fr.avenirsesr.portfolio.ams.domain.port.output.seeder;

import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.DataGeneratorInterface;

public interface CohortDataGenerator extends DataGeneratorInterface {
  String name();

  String description();
}
