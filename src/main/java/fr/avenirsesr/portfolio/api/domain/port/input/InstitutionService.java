package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;

public interface InstitutionService {
  boolean isNavigationEnabledFor(Student student, EPortfolioType navigationField);
}
