package fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.trace.domain.model.Attachment;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.AttachmentRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.AttachmentMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.AttachmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AttachmentDatabaseRepository
    extends GenericJpaRepositoryAdapter<Attachment, AttachmentEntity>
    implements AttachmentRepository {
  public AttachmentDatabaseRepository(AttachmentJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, AttachmentMapper::fromDomain, AttachmentMapper::toDomain);
  }
}
