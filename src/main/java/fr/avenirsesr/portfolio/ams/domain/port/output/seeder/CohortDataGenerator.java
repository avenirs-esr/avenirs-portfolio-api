package fr.avenirsesr.portfolio.ams.domain.port.output.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGeneratorInterface;

public interface CohortDataGenerator extends DataGeneratorInterface {
  String name();

  String description();
}
