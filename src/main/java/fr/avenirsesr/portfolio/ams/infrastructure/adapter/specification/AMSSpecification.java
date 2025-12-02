package fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSTranslationEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import org.springframework.data.jpa.domain.Specification;

public class AMSSpecification {

  public static Specification<AMSTranslationEntity> search(String keyword, ELanguage language) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      var languagePredicate = criteriaBuilder.equal(root.get("language"), language);
      String pattern = "%" + keyword.toLowerCase() + "%";
      var namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern);
      return criteriaBuilder.and(languagePredicate, namePredicate);
    };
  }
}
