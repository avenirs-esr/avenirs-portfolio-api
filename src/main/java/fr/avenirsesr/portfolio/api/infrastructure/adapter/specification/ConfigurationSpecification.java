package fr.avenirsesr.portfolio.api.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.api.domain.model.enums.EConfiguration;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ConfigurationEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ConfigurationSpecification {
  public static Specification<ConfigurationEntity> byAnyName(
      List<EConfiguration> eConfigurationList) {

    return (root, query, criteriaBuilder) -> {
      if (eConfigurationList == null || eConfigurationList.isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      List<Predicate> predicates = new ArrayList<>();
      for (EConfiguration configuration : eConfigurationList) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + configuration.name().toLowerCase() + "%"));
      }

      return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    };
  }
}
