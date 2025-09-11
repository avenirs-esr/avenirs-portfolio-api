package fr.avenirsesr.portfolio.user.domain.port.output.seeder;

import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.DataGeneratorInterface;

public interface UserDataGenerator extends DataGeneratorInterface {
  String firstName();

  String lastName();

  String email();

  String studentDescription();

  String teacherDescription();
}
