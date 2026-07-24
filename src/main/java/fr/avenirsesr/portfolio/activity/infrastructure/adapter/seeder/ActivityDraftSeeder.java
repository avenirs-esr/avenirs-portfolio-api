package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityDraftMapper;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityDraftEntity;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data.ActivityDraftCreationData;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data.FakeActivityDraft;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import jakarta.persistence.EntityManager;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ActivityDraftSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(ActivityDraftSeeder.class, SharedDataGenerator.class);

  private static final String PATH_FILE = "seeder/activities-draft.json";

  private final FileReader fileReader;
  private final ActivityService activityService;
  private final ClockService clockService;
  private final EntityManager entityManager;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public ActivityDraftSeeder(
      FileReader fileReader,
      ActivityService activityService,
      ClockService clockService,
      EntityManager entityManager) {
    this.fileReader = fileReader;
    this.activityService = activityService;
    this.clockService = clockService;
    this.entityManager = entityManager;
  }

  @Transactional
  public List<ActivityDraftEntity> seed(List<StaffEntity> savedStaffs) {
    log.info("Seeding Activities drafts...");

    List<ActivityDraftCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(
                  PATH_FILE, new TypeReference<List<ActivityDraftCreationData>>() {});

          case FAKER ->
              IntStream.range(0, SeederConfig.NB_OF_ACTIVITIES_DRAFT)
                  .mapToObj(
                      i ->
                          FakeActivityDraft.create(dataGenerator.with("author").pickIn(savedStaffs))
                              .toEntity())
                  .map(
                      fakeActivity -> {
                        var now = clockService.now();

                        return new ActivityDraftCreationData(
                            fakeActivity.getTitle(),
                            fakeActivity.getAuthor().getId(),
                            fakeActivity.getThematic(),
                            Optional.ofNullable(fakeActivity.getSummary()),
                            Optional.ofNullable(fakeActivity.getDescription()),
                            Optional.ofNullable(fakeActivity.getExecutionPeriodInfo()),
                            Optional.ofNullable(fakeActivity.getExecutionPeriodInfoSummary()),
                            Optional.ofNullable(fakeActivity.getStartDate()),
                            Optional.ofNullable(fakeActivity.getEndDate()),
                            Optional.ofNullable(fakeActivity.getTraceAllowedAssociations()),
                            Optional.ofNullable(fakeActivity.getFeedbackAllowedIterations()),
                            fakeActivity.isEnableReflection(),
                            new ArrayList<>(),
                            now,
                            now);
                      })
                  .toList();
        };

    List<ActivityDraft> drafts = new ArrayList<>();

    creationData.forEach(
        data -> {
          var author =
              savedStaffs.stream()
                  .filter(s -> s.getId().equals(data.authorStaffId()))
                  .findFirst()
                  .orElseThrow();

          RequestContext.set(
              new RequestData(
                  Optional.ofNullable(UserMapper.INSTANCE.toDomain(author.getUser())),
                  ELanguage.FRENCH));

          try {
            var now = clockService.now();
            var createdAt = now.minus(20, ChronoUnit.DAYS);
            if (data.updatedAt() != null) {
              clockService.fixed(data.updatedAt());
            } else {
              clockService.fixed(createdAt);
            }

            var draft = activityService.createActivityDraft(data.title());

            if (data.thematic() == EActivityThematic.SELF_KNOWLEDGE) {
              clockService.fixed(createdAt.plus(1, ChronoUnit.DAYS));
            }

            var updatedDraft =
                activityService.updateActivityDraft(
                    draft.getId(),
                    data.title(),
                    data.thematic(),
                    data.summary().orElse(null),
                    data.description().orElse(null),
                    data.executionPeriodInfo().orElse(null),
                    data.executionPeriodInfoSummary().orElse(null),
                    data.startDate().orElse(null),
                    data.endDate().orElse(null),
                    data.traceAllowedAssociations().orElse(null),
                    data.feedbackAllowedIterations().orElse(null),
                    data.enableReflection(),
                    data.links());

            entityManager.flush();

            drafts.add(updatedDraft);
          } finally {
            clockService.clear();
          }
        });

    log.info("✔ {} activities draft created", drafts.size());
    return drafts.stream().map(ActivityDraftMapper.INSTANCE::fromDomain).toList();
  }
}
