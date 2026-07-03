package fr.avenirsesr.portfolio.activity.domain.mapper;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.util.Optional;
import java.util.UUID;

public interface ActivityPresentationDataMapper {
  static ActivityPresentationData toData(
      Activity activity, UUID subscribedDeclaredActivity, FileData banner) {
    return new ActivityPresentationData(
        activity.getId(),
        activity.getTitle(),
        activity.getThematic(),
        Optional.ofNullable(subscribedDeclaredActivity),
        activity.getSummary(),
        activity.getDescription().orElse(null),
        activity.getExecutionPeriodInfo().orElse(null),
        banner,
        activity.getLinks(),
        activity.getCreatedAt(),
        activity.getUpdatedAt());
  }

  static ActivityPresentationData toData(ActivityDraft draft, FileData banner) {
    return new ActivityPresentationData(
        draft.getId(),
        draft.getTitle(),
        draft.getThematic(),
        Optional.empty(),
        draft.getSummary().orElse(null),
        draft.getDescription().orElse(null),
        draft.getExecutionPeriodInfo().orElse(null),
        banner,
        draft.getLinks(),
        draft.getCreatedAt(),
        draft.getUpdatedAt());
  }
}
