package fr.avenirsesr.portfolio.association.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssociationJpaRepository
    extends JpaRepository<AssociationEntity, UUID>, JpaSpecificationExecutor<AssociationEntity> {}
