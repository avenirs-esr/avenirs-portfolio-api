package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.TraceAttachmentMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.specification.TraceAttachmentSpecification;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class TraceAttachmentDatabaseRepository
    extends GenericJpaRepositoryAdapter<TraceAttachment, TraceAttachmentEntity>
    implements TraceAttachmentRepository {
  public TraceAttachmentDatabaseRepository(TraceAttachmentJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        TraceAttachmentMapper::fromDomain,
        TraceAttachmentMapper::toDomain);
  }

  @Override
  public List<TraceAttachment> findByTrace(Trace trace) {
    return findAll(TraceAttachmentSpecification.ofTrace(trace));
  }
}
