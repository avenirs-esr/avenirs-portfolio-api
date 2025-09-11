package fr.avenirsesr.portfolio.shared.domain.port.output.seeder;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class DataGenerator {
  private Random random;
  private ELanguage language;

  //  // -- Program
  //  String university();
  //
  //  String program();
  //
  //  String skill();
  //
  //  String skillLevelName();
  //
  //  String skillLevelDescription();
  //
  //  // -- AMS
  //  String ams();
  //
  //  String cohortName();
  //
  //  String cohortDescription();
  //
  //  // -- Files
  //  String fileName(EFileType fileType);
  //
  //  // -- Users
  //  String firstName();
  //
  //  String lastName();
  //
  //  String studentDescription();
  //
  //  String teacherDescription();
  //
  //  String email();

}
