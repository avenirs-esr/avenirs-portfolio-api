package fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import jakarta.persistence.criteria.*;
import java.util.Set;
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

  public static Specification<TraceEntity> excludeIds(Set<UUID> excludedIds) {
    return (root, query, cb) -> cb.not(root.get("id").in(excludedIds));
  }

  public static Specification<TraceEntity> idsIn(Set<UUID> idsIn) {
    return (root, query, cb) -> cb.in(root.get("id").in(idsIn));
  }

  public static Specification<TraceEntity> ofAms(AMS ams) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.isMember(AMSMapper.INSTANCE.fromDomain(ams), root.get("amses"));
  }

  public static Specification<TraceEntity> ofSkillLevelProgress(
      SkillLevelProgress skillLevelProgress) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.isMember(
            SkillLevelProgressMapper.INSTANCE.fromDomain(skillLevelProgress),
            root.get("skillLevels"));
  }

  public static Specification<TraceEntity> ofDeclaredSkillProgress(
      DeclaredSkillProgress declaredSkillProgress) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.isMember(
            DeclaredSkillProgressMapper.INSTANCE.fromDomain(declaredSkillProgress),
            root.get("declaredSkillsProgresses"));
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
      Subquery<TraceAttachmentEntity> attachSub = query.subquery(TraceAttachmentEntity.class);
      Root<TraceAttachmentEntity> attachRoot = attachSub.from(TraceAttachmentEntity.class);
      attachSub
          .select(attachRoot)
          .where(
              criteriaBuilder.equal(attachRoot.get("trace").get("id"), root.get("id")),
              criteriaBuilder.isTrue(attachRoot.get("isActiveVersion")),
              criteriaBuilder.like(criteriaBuilder.lower(attachRoot.get("name")), pattern));
      Predicate attachmentPredicate = criteriaBuilder.exists(attachSub);

      // Declared skills
      var declaredSkillJoin =
          root.join("declaredSkillsProgresses", JoinType.LEFT).join("declaredSkill", JoinType.LEFT);

      var declaredSkillPredicate =
          criteriaBuilder.like(criteriaBuilder.lower(declaredSkillJoin.get("libelle")), pattern);

      // Skill level
      var slpJoin =
          root.join("skillLevels", JoinType.LEFT)
              .join("skillLevel", JoinType.LEFT)
              .join("translations", JoinType.LEFT);
      var slpLangPredicate = criteriaBuilder.equal(slpJoin.get("language"), language);
      var slpNamePredicate =
          criteriaBuilder.like(criteriaBuilder.lower(slpJoin.get("name")), pattern);
      var slpDescPredicate =
          criteriaBuilder.like(criteriaBuilder.lower(slpJoin.get("description")), pattern);
      var skillLevelPredicate =
          criteriaBuilder.and(
              slpLangPredicate, criteriaBuilder.or(slpNamePredicate, slpDescPredicate));

      // AMS
      var amsJoin = root.join("amses", JoinType.LEFT).join("translations", JoinType.LEFT);
      var amsLangPredicate = criteriaBuilder.equal(amsJoin.get("language"), language);
      var amsTitlePredicate =
          criteriaBuilder.like(criteriaBuilder.lower(amsJoin.get("title")), pattern);
      var amsPredicate = criteriaBuilder.and(amsLangPredicate, amsTitlePredicate);

      return criteriaBuilder.or(
          titlePredicate,
          aiUsePredicate,
          personalNotePredicate,
          attachmentPredicate,
          declaredSkillPredicate,
          skillLevelPredicate,
          amsPredicate);
    };
  }
}
