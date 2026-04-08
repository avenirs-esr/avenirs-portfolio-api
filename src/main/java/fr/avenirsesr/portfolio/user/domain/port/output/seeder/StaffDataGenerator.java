package fr.avenirsesr.portfolio.user.domain.port.output.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGeneratorInterface;

public interface StaffDataGenerator extends DataGeneratorInterface {
  String staffDescription();
}
