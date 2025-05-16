package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AMSJpaRepository extends JpaRepository<AMSEntity, UUID> {
}
