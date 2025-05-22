package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProgramProgressJpaRepository
    extends JpaRepository<ProgramProgressEntity, UUID>,
        JpaSpecificationExecutor<ProgramProgressEntity> {}
