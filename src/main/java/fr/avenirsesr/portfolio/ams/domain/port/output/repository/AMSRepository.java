package fr.avenirsesr.portfolio.ams.domain.port.output.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import java.util.UUID;

public interface AMSRepository extends GenericRepositoryPort<AMS> {
  PagedResult<AMS> findByUserIdViaCohortsAndProgramProgressId(
      UUID userId, UUID programProgressId, int page, int size);
}
