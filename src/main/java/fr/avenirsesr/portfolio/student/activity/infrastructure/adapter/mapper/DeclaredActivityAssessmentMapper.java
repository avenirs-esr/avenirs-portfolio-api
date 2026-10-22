package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivityAssessment;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityAssessmentEmbeddable;

public class DeclaredActivityAssessmentMapper
    implements Mapper<DeclaredActivityAssessmentEmbeddable, DeclaredActivityAssessment> {
  public static final DeclaredActivityAssessmentMapper INSTANCE =
      new DeclaredActivityAssessmentMapper();

  @Override
  public DeclaredActivityAssessmentEmbeddable fromDomain(DeclaredActivityAssessment domain) {
    return new DeclaredActivityAssessmentEmbeddable(
        domain.comment(), domain.grade(), domain.validation());
  }

  @Override
  public DeclaredActivityAssessment toDomain(DeclaredActivityAssessmentEmbeddable entity) {
    return new DeclaredActivityAssessment(
        entity.getComment(), entity.getGrade(), entity.getValidation());
  }
}
