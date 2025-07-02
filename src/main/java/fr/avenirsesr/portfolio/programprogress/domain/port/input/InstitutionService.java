package fr.avenirsesr.portfolio.programprogress.domain.port.input;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.user.domain.model.Student;

public interface InstitutionService {
  boolean isNavigationEnabledFor(Student student, EPortfolioType navigationField);
}
