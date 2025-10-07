package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class AdditionalSkillProgressSpecification {
  public static Specification<AdditionalSkillProgressEntity> additionalSkillProgressAlreadyExists(
      AdditionalSkill additionalSkill, UUID studentId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(
                root.get("additionalSkill"), AdditionalSkillMapper.fromDomain(additionalSkill)),
            criteriaBuilder.equal(root.get("student").get("id"), studentId));
  }

  public static Specification<AdditionalSkillProgressEntity> hasStudent(UserEntity student) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("student"), student);
  }

  public static Specification<AdditionalSkillProgressEntity> search(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("additionalSkill").get("libelle")),
          "%" + keyword.toLowerCase() + "%");
    };
  }
}
