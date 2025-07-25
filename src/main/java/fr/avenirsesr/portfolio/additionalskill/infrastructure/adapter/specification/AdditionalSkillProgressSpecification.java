package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
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

  public static Specification<AdditionalSkillProgressEntity> findAllByStudent(UUID studentId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("student").get("id"), studentId);
  }
}
