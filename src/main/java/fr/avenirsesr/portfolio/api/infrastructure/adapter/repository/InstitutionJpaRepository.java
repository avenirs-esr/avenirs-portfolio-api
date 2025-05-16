package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.InstitutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstitutionJpaRepository extends JpaRepository<InstitutionEntity, UUID> {
}
