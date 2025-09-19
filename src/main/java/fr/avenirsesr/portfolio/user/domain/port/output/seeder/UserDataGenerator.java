package fr.avenirsesr.portfolio.user.domain.port.output.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.DataGeneratorInterface;

public interface UserDataGenerator extends DataGeneratorInterface {
  String firstName();

  String lastName();

  String email();

  String studentDescription();

  String teacherDescription();
}
