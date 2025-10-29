package fr.avenirsesr.portfolio.program.domain.port.input;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;

public interface InstitutionService {
  boolean isNavigationEnabledFor(EPortfolioType navigationField);
}
