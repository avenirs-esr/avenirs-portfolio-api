package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackJpaRepository extends JpaRepository<TrackEntity, UUID> {}
