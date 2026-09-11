package fr.avenirsesr.portfolio.user.domain.port.output.client;

import fr.avenirsesr.portfolio.common.institution.application.adapter.dto.InstitutionDTO;
import java.util.Optional;
import java.util.UUID;

public interface InstitutionClient {
  Optional<InstitutionDTO> getById(UUID id);
}
