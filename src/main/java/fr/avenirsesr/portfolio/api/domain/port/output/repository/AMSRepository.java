package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import java.util.List;
import java.util.UUID;

public interface AMSRepository extends GenericRepositoryPort<AMS> {
  List<AMS> findByUserIdViaCohorts(UUID userId, int page, int size);
}
