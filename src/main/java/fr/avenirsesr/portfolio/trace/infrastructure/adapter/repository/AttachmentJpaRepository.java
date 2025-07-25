package fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.AttachmentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttachmentJpaRepository
    extends JpaRepository<AttachmentEntity, UUID>, JpaSpecificationExecutor<AttachmentEntity> {}
