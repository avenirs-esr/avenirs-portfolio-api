package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class AdditionalSkillProgressSpecification {
  public static Specification<AdditionalSkillProgressEntity> additionalSkillProgressAlreadyExists(
      UUID additionalSkillId, UUID studentId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get("additionalSkillId"), additionalSkillId),
            criteriaBuilder.equal(root.get("student").get("id"), studentId));
  }

  public static Specification<AdditionalSkillProgressEntity> hasStudent(UserEntity student) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("student"), student);
  }
}
