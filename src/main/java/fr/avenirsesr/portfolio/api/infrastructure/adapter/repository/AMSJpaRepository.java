package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AMSJpaRepository extends JpaRepository<AMSEntity, UUID> {}
