package fr.avenirsesr.portfolio.file.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import org.springframework.data.jpa.domain.Specification;

public interface TraceAttachmentSpecification {
  static Specification<TraceAttachmentEntity> ofTrace(Trace trace) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("trace"), TraceMapper.INSTANCE.fromDomain(trace));
  }
}
