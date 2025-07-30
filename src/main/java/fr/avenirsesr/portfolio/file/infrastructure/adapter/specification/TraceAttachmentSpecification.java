package fr.avenirsesr.portfolio.file.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import org.springframework.data.jpa.domain.Specification;

public interface TraceAttachmentSpecification {
  static Specification<TraceAttachmentEntity> ofTrace(TraceEntity trace) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("trace"), trace);
  }
}
