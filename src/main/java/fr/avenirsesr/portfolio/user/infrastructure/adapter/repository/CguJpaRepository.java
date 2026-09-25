package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.CguEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CguJpaRepository
    extends JpaRepository<CguEntity, UUID>, JpaSpecificationExecutor<CguEntity> {
  Optional<CguEntity> findFirstByUserIdOrderByAcceptedAtDesc(UUID userId);

  Optional<CguEntity> findByUserIdAndVersionId(UUID userId, UUID versionId);
}
