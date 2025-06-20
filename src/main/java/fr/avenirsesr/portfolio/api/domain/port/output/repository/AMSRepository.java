package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import java.util.UUID;

public interface AMSRepository extends GenericRepositoryPort<AMS> {
  PagedResult<AMS> findByUserIdViaCohorts(UUID userId, int page, int size);
}
