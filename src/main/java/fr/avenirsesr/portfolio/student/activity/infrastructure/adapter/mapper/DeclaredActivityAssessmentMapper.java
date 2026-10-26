package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivityAssessment;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityAssessmentEmbeddable;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;

public class DeclaredActivityAssessmentMapper
    implements Mapper<DeclaredActivityAssessmentEmbeddable, DeclaredActivityAssessment> {
  public static final DeclaredActivityAssessmentMapper INSTANCE =
      new DeclaredActivityAssessmentMapper();

  @Override
  public DeclaredActivityAssessmentEmbeddable fromDomain(DeclaredActivityAssessment domain) {
    return new DeclaredActivityAssessmentEmbeddable(
        domain.comment(),
        domain.grade(),
        domain.validation(),
        domain.requestedAt(),
        domain.assessedAt(),
        domain.assessedBy() == null ? null : StaffMapper.INSTANCE.fromDomain(domain.assessedBy()));
  }

  @Override
  public DeclaredActivityAssessment toDomain(DeclaredActivityAssessmentEmbeddable entity) {
    return new DeclaredActivityAssessment(
        entity.getComment(),
        entity.getGrade(),
        entity.getValidation(),
        entity.getRequestedAt(),
        entity.getAssessedAt(),
        entity.getAssessedBy() == null
            ? null
            : StaffMapper.INSTANCE.toDomain(entity.getAssessedBy()));
  }
}
