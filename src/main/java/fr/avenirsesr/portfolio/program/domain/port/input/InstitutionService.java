package fr.avenirsesr.portfolio.program.domain.port.input;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import java.util.UUID;

public interface InstitutionService {
  InstitutionConfigurationElements getInstitutionConfiguration();

  Institution createInstitution(UUID institutionId, String name);

  Institution updateInstitution(UUID institutionId, String name);
}
