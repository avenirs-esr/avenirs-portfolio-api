package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProgramJpaRepository extends JpaRepository<ProgramEntity, UUID> {
}
