package fr.avenirsesr.portfolio.program.domain.port.input;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.program.domain.model.Institution;

public interface InstitutionService {
  InstitutionConfigurationElements getInstitutionConfiguration();

  Institution createInstitution(String name);
}
