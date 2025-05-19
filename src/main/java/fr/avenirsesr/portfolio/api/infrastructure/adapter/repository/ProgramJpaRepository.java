package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramJpaRepository extends JpaRepository<ProgramEntity, UUID> {}
