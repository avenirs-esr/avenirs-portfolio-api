package fr.avenirsesr.portfolio.user.domain.port.output.client;

import fr.avenirsesr.portfolio.common.cgu.application.adapter.dto.CguDTO;
import java.util.Optional;

public interface CguClient {
  Optional<CguDTO> getLatest();
}
