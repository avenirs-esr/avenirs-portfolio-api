package fr.avenirsesr.portfolio.activity.domain.mapper;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityContentData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.file.domain.data.FileData;

public interface ActivityContentDataMapper {
  static ActivityContentData toData(Activity activity, FileData banner) {
    return new ActivityContentData(
        activity.getId(),
        activity.getTitle(),
        activity.getThematic(),
        banner,
        activity.getSummary(),
        activity.getDescription().orElse(null),
        activity.getExecutionPeriodInfo().orElse(null),
        activity.isEnableReflection(),
        activity.getTraceAllowedAssociations(),
        activity.getFeedbackAllowedIterations(),
        activity.getCreatedAt(),
        activity.getUpdatedAt());
  }

  static ActivityContentData toData(ActivityDraft draft, FileData banner) {
    return new ActivityContentData(
        draft.getId(),
        draft.getTitle(),
        draft.getThematic(),
        banner,
        draft.getSummary().orElse(null),
        draft.getDescription().orElse(null),
        draft.getExecutionPeriodInfo().orElse(null),
        draft.isEnableReflection(),
        draft.getTraceAllowedAssociations(),
        draft.getFeedbackAllowedIterations(),
        draft.getCreatedAt(),
        draft.getUpdatedAt());
  }
}
