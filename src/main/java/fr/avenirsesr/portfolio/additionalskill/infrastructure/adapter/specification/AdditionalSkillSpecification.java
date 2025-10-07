package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class AdditionalSkillSpecification {
  public static Specification<AdditionalSkillEntity> hasExternalId(List<String> externalIds) {
    return (root, query, criteriaBuilder) -> {
      if (externalIds == null || externalIds.isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return root.get("externalId").in(externalIds);
    };
  }
}
