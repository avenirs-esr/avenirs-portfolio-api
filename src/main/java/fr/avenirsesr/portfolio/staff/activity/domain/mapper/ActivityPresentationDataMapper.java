package fr.avenirsesr.portfolio.staff.activity.domain.mapper;

import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.SubActivityOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityPresentationDataMapper {
  static ActivityPresentationData toData(
      Activity activity,
      EActivityStatus activityStatus,
      UUID subscribedDeclaredActivity,
      EDeclaredActivityStatus subscribedDeclaredActivityStatus,
      FileData banner,
      List<SubActivityOverviewData> subActivities) {
    return new ActivityPresentationData(
        activity.getId(),
        activity.getTitle(),
        activity.getThematic(),
        activityStatus,
        Optional.ofNullable(subscribedDeclaredActivity),
        Optional.ofNullable(subscribedDeclaredActivityStatus),
        activity.getSummary(),
        activity.getDescription(),
        activity.getRecommendedCompletionContexts().orElse(null),
        banner,
        activity.getMainActivityId(),
        subActivities,
        activity.getCreatedAt(),
        activity.getUpdatedAt());
  }

  static ActivityPresentationData toData(
      ActivityDraft draft, FileData banner, List<SubActivityOverviewData> subActivities) {
    return new ActivityPresentationData(
        draft.getId(),
        draft.getTitle(),
        draft.getThematic().orElse(null),
        EActivityStatus.DRAFT,
        Optional.empty(),
        Optional.empty(),
        draft.getSummary().orElse(null),
        draft.getDescription().orElse(null),
        draft.getRecommendedCompletionContexts().orElse(null),
        banner,
        draft.getMainActivityId(),
        subActivities,
        draft.getCreatedAt(),
        draft.getUpdatedAt());
  }
}
