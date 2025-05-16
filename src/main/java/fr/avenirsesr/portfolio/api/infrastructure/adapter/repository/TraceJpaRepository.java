package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TraceJpaRepository  extends JpaRepository<TraceEntity, UUID> {
}
