package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.specification.FilterSpecificationBuilder;
import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.student.trace.domain.filter.ETraceFilterKey;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class TraceFilterSpecificationBuilder
    extends FilterSpecificationBuilder<TraceEntity, ETraceFilterKey> {

  @Override
  public Specification<TraceEntity> getSpecification(ETraceFilterKey key, Object value) {
    return switch (key) {
      case FILE_TYPE -> fileType((List<EFileType>) value);
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

  /**
   * Keeps the traces whose attachment is of one of the given types.
   *
   * <p>The attachment is reached through the {@code trace.attachment} association rather than by
   * querying the file table on an owner column: a file no longer knows which business element it
   * belongs to, that link was inverted when the file table was decoupled from the business
   * entities. The inner join also drops the traces that carry no attachment at all, which is the
   * expected outcome of filtering on a file type.
   */
  private Specification<TraceEntity> fileType(List<EFileType> fileTypes) {
    return (root, query, cb) -> {
      if (fileTypes == null || fileTypes.isEmpty() || query == null) return null;

      return root.join("attachment", JoinType.INNER).get("fileType").in(fileTypes);
    };
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
