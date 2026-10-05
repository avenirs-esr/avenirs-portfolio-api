package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.specification.FilterSpecificationBuilder;
import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.student.trace.domain.filter.ETraceFilterKey;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class TraceFilterSpecificationBuilder
    extends FilterSpecificationBuilder<TraceEntity, ETraceFilterKey> {

  @Override
  public Specification<TraceEntity> getSpecification(ETraceFilterKey key, Object value) {
    return switch (key) {
      case TYPE -> type((TraceFilter.TraceTypeFilter) value);
      case SKILL -> skill((List<UUID>) value);
      case IS_ASSOCIATED -> {
        if (value == null) yield null;
        yield ((Boolean) value)
            ? TraceSpecification.associated()
            : TraceSpecification.unassociated();
      }
      case IS_VALORIZED -> {
        if (value == null) yield null;
        yield TraceSpecification.valorized((Boolean) value);
      }
    };
  }

  private Specification<TraceEntity> type(TraceFilter.TraceTypeFilter filter) {
    return (root, query, cb) -> {
      if (filter == null || filter.isEmpty()) return null;

      Predicate linkPredicate =
          filter.isLink() == null
              ? null
              : TraceSpecification.isLink(filter.isLink()).toPredicate(root, query, cb);

      if (filter.hasNoFileTypes()) return linkPredicate;

      Predicate fileTypes = fileTypeIn(root, filter.fileTypes());

      if (linkPredicate == null) return fileTypes;

      return filter.isLink() ? cb.or(fileTypes, linkPredicate) : cb.and(fileTypes, linkPredicate);
    };
  }

  private Predicate fileTypeIn(Root<TraceEntity> root, List<EFileType> fileTypes) {
    return root.join("attachment", JoinType.LEFT).get("fileType").in(fileTypes);
  }

  private Specification<TraceEntity> skill(List<UUID> values) {
    return (root, query, cb) -> {
      if (values == null || values.isEmpty() || query == null) return null;
      var skill =
          root.join("skillLevels", JoinType.LEFT)
              .join("skillLevel", JoinType.LEFT)
              .join("skill", JoinType.LEFT);
      query.distinct(true);
      return skill.get("id").in(values);
    };
  }
}
