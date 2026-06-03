package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.mapper;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityContentData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;

public interface DeclaredActivityDetailsDataMapper {
  static DeclaredActivityDetailsData toData(
      DeclaredActivity declaredActivity, ActivityContentData activityContentData) {
    return new DeclaredActivityDetailsData(
        declaredActivity.getId(),
        activityContentData,
        declaredActivity.getStatus(),
        declaredActivity.getReflection(),
        declaredActivity.getStartDate(),
        declaredActivity.getEndDate(),
        declaredActivity.getFinishedAt().orElse(null),
        declaredActivity.getCreatedAt(),
        declaredActivity.getUpdatedAt());
  }
}
