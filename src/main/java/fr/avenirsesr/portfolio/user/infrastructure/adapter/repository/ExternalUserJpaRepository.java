package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.ExternalUserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExternalUserJpaRepository
    extends JpaRepository<ExternalUserEntity, UUID>, JpaSpecificationExecutor<ExternalUserEntity> {}
