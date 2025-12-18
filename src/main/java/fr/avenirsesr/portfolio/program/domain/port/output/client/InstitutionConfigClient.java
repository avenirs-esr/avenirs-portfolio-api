package fr.avenirsesr.portfolio.program.domain.port.output.client;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import java.util.UUID;

public interface InstitutionConfigClient {
  InstitutionConfigurationElements getInstitutionConfigElementsById(UUID institutionId);
}
