package fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.specification.FilterSpecificationBuilder;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.trace.domain.filter.ETraceFilterKey;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
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
    };
  }

  private Specification<TraceEntity> fileType(List<EFileType> fileTypes) {
    return (root, query, cb) -> {
      if (fileTypes == null || fileTypes.isEmpty() || query == null) return null;

      Subquery<TraceAttachmentEntity> attachSub = query.subquery(TraceAttachmentEntity.class);
      Root<TraceAttachmentEntity> attachRoot = attachSub.from(TraceAttachmentEntity.class);
      Predicate fileTypePredicate = attachRoot.get("fileType").in(fileTypes);
      Predicate belongsToTrace = cb.equal(attachRoot.get("trace").get("id"), root.get("id"));
      Predicate isActive = cb.isTrue(attachRoot.get("isActiveVersion"));
      attachSub.where(cb.and(belongsToTrace, isActive, fileTypePredicate));

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
        var amsJoin = root.join("amses", JoinType.LEFT);
        var skillLevelJoin = root.join("skillLevels", JoinType.LEFT);

        Predicate amsPredicate = null;
        Predicate skillPredicate = null;

        if (!status.getAmsStatuses().isEmpty()) {
          amsPredicate = amsJoin.get("status").in(status.getAmsStatuses());
        }

        if (!status.getSkillLevelStatuses().isEmpty()) {
          skillPredicate = skillLevelJoin.get("status").in(status.getSkillLevelStatuses());
        }

        if (amsPredicate != null) predicates.add(amsPredicate);
        if (skillPredicate != null) predicates.add(skillPredicate);
      }

      return cb.or(predicates.toArray(new Predicate[0]));
    };
  }
}
