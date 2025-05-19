package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraceJpaRepository extends JpaRepository<TraceEntity, UUID> {}
