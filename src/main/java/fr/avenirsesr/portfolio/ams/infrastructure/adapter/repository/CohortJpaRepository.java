package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CohortJpaRepository
    extends JpaRepository<CohortEntity, UUID>, JpaSpecificationExecutor<CohortEntity> {}
