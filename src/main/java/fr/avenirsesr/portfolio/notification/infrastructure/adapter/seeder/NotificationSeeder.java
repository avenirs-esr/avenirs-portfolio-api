package fr.avenirsesr.portfolio.notification.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.seeder.data.ActivityUpdatedNotificationCreationData;
import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSeeder {
  private static final String ACTIVITY_UPDATED_PATH_FILE =
      "seeder/activity-updated-notifications.json";

  private final FileReader fileReader;
  private final ActivityService activityService;
  private final UserService userService;
  private final ClockService clockService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public void seed(
      List<ActivityEntity> savedActivities, List<DeclaredActivityEntity> savedDeclaredActivities) {
    log.info("Seeding Notifications...");
    seedActivityUpdatedNotifications(savedActivities, savedDeclaredActivities);
  }

  private void seedActivityUpdatedNotifications(
      List<ActivityEntity> savedActivities, List<DeclaredActivityEntity> savedDeclaredActivities) {
    List<ActivityUpdatedNotificationCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(
                  ACTIVITY_UPDATED_PATH_FILE,
                  new TypeReference<List<ActivityUpdatedNotificationCreationData>>() {});
          case FAKER ->
              savedDeclaredActivities.stream()
                  .collect(
                      Collectors.toMap(
                          declaredActivity -> declaredActivity.getActivity().getId(),
                          Function.identity(),
                          (first, second) -> first,
                          LinkedHashMap::new))
                  .values()
                  .stream()
                  .limit(SeederConfig.NB_OF_ACTIVITY_UPDATED_NOTIFICATIONS)
                  .map(
                      declaredActivity ->
                          new ActivityUpdatedNotificationCreationData(
                              declaredActivity.getActivity().getId(),
                              declaredActivity.getStudent().getId(),
                              clockService.now(),
                              List.of(EActivityUpdatableField.ACTIVITY_TITLE)))
                  .toList();
        };

    creationData.forEach(
        data -> {
          enableNotificationPreferences(data.studentId());
          try {
            clockService.fixed(data.createdAt());
            republishWithModifiedFields(data.activityId(), data.updatedFields(), savedActivities);
          } finally {
            clockService.clear();
          }
        });

    log.info("✔ {} activity updated notifications created", creationData.size());
  }

  private void enableNotificationPreferences(UUID studentId) {
    var student = userService.getUser(studentId);
    RequestContext.set(new RequestData(Optional.of(student), ELanguage.FRENCH));
    userService.updateNotificationPreferences(true);
  }

  private void republishWithModifiedFields(
      UUID activityId,
      List<EActivityUpdatableField> updatedFields,
      List<ActivityEntity> savedActivities) {
    var activity =
        savedActivities.stream()
            .filter(a -> a.getId().equals(activityId))
            .findFirst()
            .orElseThrow();

    RequestContext.set(
        new RequestData(
            Optional.ofNullable(UserMapper.INSTANCE.toDomain(activity.getAuthor().getUser())),
            ELanguage.FRENCH));

    var draft = activityService.createDraftFromActivity(activityId);

    String title = null;
    EActivityThematic thematic = null;
    String summary = null;
    String description = null;
    String recommendedCompletionContexts = null;
    List<String> links = null;

    for (var field : updatedFields) {
      switch (field) {
        case ACTIVITY_TITLE -> title = draft.getTitle() + " (modifié)";
        case THEMATIC -> thematic = nextThematic(draft.getThematic());
        case SUMMARY -> summary = draft.getSummary().orElse("") + " (modifié)";
        case DESCRIPTION -> description = draft.getDescription().orElse("") + " (modifié)";
        case RECOMMENDED_COMPLETION_CONTEXTS ->
            recommendedCompletionContexts =
                draft.getRecommendedCompletionContexts().orElse("") + " (modifié)";
        case FILES_AND_LINKS ->
            links =
                Stream.concat(draft.getLinks().stream(), Stream.of("https://example.com/modifie"))
                    .toList();
        case BANNER ->
            throw new UnsupportedOperationException(
                "BANNER updates are not supported by the notification seeder");
      }
    }

    activityService.updateActivityDraft(
        draft.getId(),
        title,
        thematic,
        summary,
        description,
        recommendedCompletionContexts,
        null,
        null,
        null,
        null,
        null,
        links);

    activityService.publish(draft.getId());
  }

  private EActivityThematic nextThematic(EActivityThematic current) {
    var values = EActivityThematic.values();
    return values[(current.ordinal() + 1) % values.length];
  }
}
