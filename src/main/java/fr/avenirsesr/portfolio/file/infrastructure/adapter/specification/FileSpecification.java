package fr.avenirsesr.portfolio.file.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public interface FileSpecification {
  static Specification<FileEntity> ofElement(UUID elementId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(criteriaBuilder.equal(root.get("elementId"), elementId));
  }
}
