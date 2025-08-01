package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.Rome4VersionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface Rome4VersionJpaRepository
    extends JpaRepository<Rome4VersionEntity, UUID>, JpaSpecificationExecutor<Rome4VersionEntity> {
  Optional<Rome4Version> findFirstByOrderByVersionDesc();
}
