package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TraceAttachmentJpaRepository
    extends JpaRepository<TraceAttachmentEntity, UUID>,
        JpaSpecificationExecutor<TraceAttachmentEntity> {}
