package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ExternalUserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalUserJpaRepository extends JpaRepository<ExternalUserEntity, UUID> {}
