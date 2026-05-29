package fr.avenirsesr.portfolio.user.domain.port.output.client;

import fr.avenirsesr.portfolio.common.user.application.adapter.dto.ExternalUserDTO;
import java.util.Optional;

public interface ExternalUserClient {
  Optional<ExternalUserDTO> getByEppn(String eppn);

  void activateByEppn(String eppn);
}
