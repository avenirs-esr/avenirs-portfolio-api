package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAssessmentEmbeddable;

public class ActivityAssessmentMapper
    implements Mapper<ActivityAssessmentEmbeddable, ActivityAssessment> {
  public static final ActivityAssessmentMapper INSTANCE = new ActivityAssessmentMapper();

  @Override
  public ActivityAssessmentEmbeddable fromDomain(ActivityAssessment domain) {
    return new ActivityAssessmentEmbeddable(domain.comment(), domain.grade(), domain.validation());
  }

  @Override
  public ActivityAssessment toDomain(ActivityAssessmentEmbeddable entity) {
    return new ActivityAssessment(entity.getComment(), entity.getGrade(), entity.getValidation());
  }
}
