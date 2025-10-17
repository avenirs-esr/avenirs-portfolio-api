package fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import org.springframework.data.jpa.domain.Specification;

public class AMSSpecification {

  public static Specification<AMSEntity> belongsTo(Student student) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("student"), StudentMapper.fromDomain(student));
  }

  public static Specification<AMSEntity> search(String keyword, ELanguage language) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      var translationJoin = root.join("translations");
      var languagePredicate = criteriaBuilder.equal(translationJoin.get("language"), language);
      String pattern = "%" + keyword.toLowerCase() + "%";
      var namePredicate =
          criteriaBuilder.like(criteriaBuilder.lower(translationJoin.get("title")), pattern);
      return criteriaBuilder.and(languagePredicate, namePredicate);
    };
  }
}
