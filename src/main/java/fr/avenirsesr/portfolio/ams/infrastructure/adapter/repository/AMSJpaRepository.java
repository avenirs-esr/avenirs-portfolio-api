package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AMSJpaRepository
    extends JpaRepository<AMSEntity, UUID>, JpaSpecificationExecutor<AMSEntity> {}
