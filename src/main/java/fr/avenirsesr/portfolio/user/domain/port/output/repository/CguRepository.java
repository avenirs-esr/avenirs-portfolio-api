package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.Cgu;
import java.util.Optional;
import java.util.UUID;

public interface CguRepository extends GenericRepositoryPort<Cgu> {
  Optional<Cgu> findLastAcceptedByUser(UUID userId);

  Optional<Cgu> findByUserAndVersion(UUID userId, UUID versionId);
}
