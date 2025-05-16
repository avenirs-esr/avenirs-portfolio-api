package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ExternalUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExternalUserJpaRepository extends JpaRepository<ExternalUserEntity, UUID> {
}
