package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.InstitutionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstitutionJpaRepository
    extends JpaRepository<InstitutionEntity, UUID>, JpaSpecificationExecutor<InstitutionEntity> {}
