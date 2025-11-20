package fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TraceJpaRepository
    extends JpaRepository<TraceEntity, UUID>, JpaSpecificationExecutor<TraceEntity> {}
