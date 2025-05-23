package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;

public interface ProgramProgressService {
  boolean isStudentFollowingAProgram(Student student, EPortfolioType featureType);
}
