package fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class TraceSpecification {
  public static Specification<TraceEntity> ofUser(UserEntity user) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
  }

  public static Specification<TraceEntity> unassociated() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.not(associated().toPredicate(root, query, criteriaBuilder));
  }

  public static Specification<TraceEntity> associated() {
    return (root, query, criteriaBuilder) -> {
      if (query == null) return null;

      Subquery<SkillLevelEntity> skillLevelSubquery = query.subquery(SkillLevelEntity.class);
      Root<TraceEntity> skillLevelSubRoot = skillLevelSubquery.from(TraceEntity.class);
      Join<TraceEntity, SkillLevelEntity> skillLevelJoin = skillLevelSubRoot.join("skillLevels");
      skillLevelSubquery
          .select(skillLevelJoin)
          .where(criteriaBuilder.equal(skillLevelSubRoot.get("id"), root.get("id")));

      Subquery<AMSEntity> amsSubquery = query.subquery(AMSEntity.class);
      Root<TraceEntity> amsSubRoot = amsSubquery.from(TraceEntity.class);
      Join<TraceEntity, AMSEntity> amsJoin = amsSubRoot.join("amses");
      amsSubquery
          .select(amsJoin)
          .where(criteriaBuilder.equal(amsSubRoot.get("id"), root.get("id")));

      Subquery<AdditionalSkillProgressEntity> addSkillSubquery =
          query.subquery(AdditionalSkillProgressEntity.class);
      Root<TraceEntity> addSkillSubRoot = addSkillSubquery.from(TraceEntity.class);
      Join<TraceEntity, AdditionalSkillProgressEntity> addSkillJoin =
          addSkillSubRoot.join("additionalSkillsProgresses");
      addSkillSubquery
          .select(addSkillJoin)
          .where(criteriaBuilder.equal(addSkillSubRoot.get("id"), root.get("id")));

      Predicate hasSkillLevels = criteriaBuilder.exists(skillLevelSubquery);
      Predicate hasAmses = criteriaBuilder.exists(amsSubquery);
      Predicate noAdditionalSkills = criteriaBuilder.exists(addSkillSubquery);

      query.distinct(true);

      return criteriaBuilder.or(hasSkillLevels, hasAmses, noAdditionalSkills);
    };
  }

  public static Specification<TraceEntity> notDeleted() {
    return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
  }

  public static Specification<TraceEntity> ofAms(AMSEntity ams) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(ams, root.get("amses"));
  }

  public static Specification<TraceEntity> ofSkillLevelProgress(
      SkillLevelProgressEntity skillLevelProgress) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.isMember(skillLevelProgress, root.get("skillLevels"));
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

      // Additional skills
      var additionalSkillJoin =
          root.join("additionalSkillsProgresses", JoinType.LEFT)
              .join("additionalSkill", JoinType.LEFT);

      var additionalSkillPredicate =
          criteriaBuilder.like(criteriaBuilder.lower(additionalSkillJoin.get("libelle")), pattern);

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
          additionalSkillPredicate,
          skillLevelPredicate,
          amsPredicate);
    };
  }
}
