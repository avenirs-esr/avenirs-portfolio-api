package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.AttachmentMapper;
import org.springframework.stereotype.Component;

@Component
public class TraceAttachmentDatabaseRepository
    extends GenericJpaRepositoryAdapter<TraceAttachment, TraceAttachmentEntity>
    implements TraceAttachmentRepository {
  public TraceAttachmentDatabaseRepository(TraceAttachmentJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, AttachmentMapper::fromDomain, AttachmentMapper::toDomain);
  }
}
