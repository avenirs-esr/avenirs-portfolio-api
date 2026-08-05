package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.specification.FilterSpecificationBuilder;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.student.trace.domain.filter.ETraceFilterKey;
import fr.avenirsesr.portfolio.student.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
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
      case STATUS -> status((List<ETraceStatus>) value);
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

  private Specification<TraceEntity> fileType(List<EFileType> fileTypes) {
    return (root, query, cb) -> {
      if (fileTypes == null || fileTypes.isEmpty() || query == null) return null;

      Subquery<FileEntity> attachSub = query.subquery(FileEntity.class);
      Root<FileEntity> attachRoot = attachSub.from(FileEntity.class);
      Predicate fileTypePredicate = attachRoot.get("fileType").in(fileTypes);
      Predicate belongsToTrace = cb.equal(attachRoot.get("elementId"), root.get("id"));
      attachSub.where(cb.and(belongsToTrace, fileTypePredicate));

      return cb.exists(attachSub);
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

  private Specification<TraceEntity> status(List<ETraceStatus> traceStatus) {
    return (root, query, cb) -> {
      if (traceStatus == null || query == null) return null;

      query.distinct(true);
      List<Predicate> predicates = new ArrayList<>();
      for (ETraceStatus status : traceStatus) {
        var skillLevelJoin = root.join("skillLevels", JoinType.LEFT);

        Predicate skillPredicate = null;

        if (!status.getSkillLevelStatuses().isEmpty()) {
          skillPredicate = skillLevelJoin.get("status").in(status.getSkillLevelStatuses());
        }

        if (skillPredicate != null) predicates.add(skillPredicate);
      }

      return cb.or(predicates.toArray(new Predicate[0]));
    };
  }
}
