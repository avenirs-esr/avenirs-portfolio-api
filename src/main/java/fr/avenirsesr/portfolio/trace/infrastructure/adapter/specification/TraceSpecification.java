package fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import jakarta.persistence.criteria.*;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class TraceSpecification {
  public static Specification<TraceEntity> ofUser(User user) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("user"), UserMapper.INSTANCE.fromDomain(user));
  }

  public static Specification<TraceEntity> unassociated() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.not(associated().toPredicate(root, query, criteriaBuilder));
  }

  public static Specification<TraceEntity> associated() {
    return (root, query, cb) -> {
      if (query == null) return null;

      Subquery<UUID> subquery = query.subquery(UUID.class);
      Root<AssociationEntity> associationRoot = subquery.from(AssociationEntity.class);

      var traceId = root.get("id");

      var id1Match = cb.equal(associationRoot.get("id1"), traceId);
      var id2Match = cb.equal(associationRoot.get("id2"), traceId);

      var typeIn =
          associationRoot.get("associationType").in(EAssociationType.getAllBy(Trace.class));

      subquery.select(associationRoot.get("id")).where(cb.and(cb.or(id1Match, id2Match), typeIn));

      query.distinct(true);

      return cb.exists(subquery);
    };
  }

  public static Specification<TraceEntity> notDeleted() {
    return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
  }

  public static Specification<TraceEntity> search(String keyword, ELanguage language) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty() || query == null) {
        return criteriaBuilder.conjunction();
      }

      query.distinct(true);

      String pattern = "%" + keyword.toLowerCase() + "%";

      // Trace
      var titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
      var aiUsePredicate =
          criteriaBuilder.like(criteriaBuilder.lower(root.get("aiUseJustification")), pattern);
      var personalNotePredicate =
          criteriaBuilder.like(criteriaBuilder.lower(root.get("personalNote")), pattern);

      // Attachment
      Subquery<FileEntity> attachSub = query.subquery(FileEntity.class);
      Root<FileEntity> attachRoot = attachSub.from(FileEntity.class);
      attachSub
          .select(attachRoot)
          .where(
              criteriaBuilder.equal(attachRoot.get("elementId"), root.get("id")),
              criteriaBuilder.isTrue(attachRoot.get("isActiveVersion")),
              criteriaBuilder.like(criteriaBuilder.lower(attachRoot.get("fileName")), pattern));
      Predicate attachmentPredicate = criteriaBuilder.exists(attachSub);

      return criteriaBuilder.or(
          titlePredicate, aiUsePredicate, personalNotePredicate, attachmentPredicate);
    };
  }
}
