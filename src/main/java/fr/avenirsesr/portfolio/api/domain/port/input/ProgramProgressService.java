package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELearningMethod;

public interface ProgramProgressService {
  boolean isStudentFollowingAProgram(Student student, ELearningMethod featureType);
}
