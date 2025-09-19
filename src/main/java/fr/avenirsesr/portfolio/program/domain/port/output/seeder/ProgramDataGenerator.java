package fr.avenirsesr.portfolio.program.domain.port.output.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGeneratorInterface;

public interface ProgramDataGenerator extends DataGeneratorInterface {
  String university();

  String program();

  String skill();

  String skillLevelName();

  String skillLevelDescription();
}
