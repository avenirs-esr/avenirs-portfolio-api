package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import jakarta.persistence.criteria.Join;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class SkillLevelProgressSpecification {
  public static Specification<SkillLevelProgressEntity> linkedTo(AMSEntity ams) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(ams, root.get("amses"));
  }

  public static Specification<SkillLevelProgressEntity> with(Student student) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("student"), UserMapper.fromDomain(student));
  }

  public static Specification<SkillLevelProgressEntity> with(Student student, UUID skillId) {
    return (root, query, criteriaBuilder) -> {
      var studentPredicate =
          criteriaBuilder.equal(root.get("student"), UserMapper.fromDomain(student));
      Join<Object, Object> skillEntity = root.join("skillLevel").join("skill");
      var skillPredicate = criteriaBuilder.equal(skillEntity.get("id"), skillId);

      return criteriaBuilder.and(studentPredicate, skillPredicate);
    };
  }

  public static Specification<SkillLevelProgressEntity> search(String keyword, ELanguage language) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      var skillLevelJoin = root.join("skillLevel");
      var translationJoin = skillLevelJoin.join("translations");

      var languagePredicate = criteriaBuilder.equal(translationJoin.get("language"), language);

      String pattern = "%" + keyword.toLowerCase() + "%";
      var namePredicate =
          criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("name")), pattern);
      var descriptionPredicate =
          criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("description")), pattern);

      return criteriaBuilder.and(
          languagePredicate, criteriaBuilder.or(namePredicate, descriptionPredicate));
    };
  }
}
