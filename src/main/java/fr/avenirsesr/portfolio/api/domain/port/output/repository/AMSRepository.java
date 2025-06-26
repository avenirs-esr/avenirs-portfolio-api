package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import java.util.UUID;

public interface AMSRepository extends GenericRepositoryPort<AMS> {
  PagedResult<AMS> findByUserIdViaCohortsAndProgramProgressId(
      UUID userId, UUID programProgressId, int page, int size);
}
