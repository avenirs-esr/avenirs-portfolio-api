package fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data.ActivityBannerCreationData;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data.ActivityCreationData;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.data.FakeActivity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeActivityBanner;
import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class ActivitySeeder {
  private static final String PATH_FILE = "seeder/activities.json";

  private final FileReader fileReader;
  private final ActivityService activityService;
  private final FileResourceService fileResourceService;
  private final ClockService clockService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public ActivitySeeder(
      FileReader fileReader,
      ActivityService activityService,
      @Qualifier("MockFileResourceService") FileResourceService fileResourceService,
      ClockService clockService) {
    this.fileReader = fileReader;
    this.activityService = activityService;
    this.fileResourceService = fileResourceService;
    this.clockService = clockService;
  }

  @Transactional
  public List<ActivityEntity> seed(StaffEntity uploader) {
    log.info("Seeding Activities...");

    List<ActivityCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<ActivityCreationData>>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.NB_OF_ACTIVITIES)
                  .mapToObj(i -> FakeActivity.create(uploader).toEntity())
                  .map(
                      fakeActivity -> {
                        var banner = FakeActivityBanner.create(fakeActivity).toEntity();
                        var now = clockService.now();

                        return new ActivityCreationData(
                            fakeActivity.getId(),
                            uploader.getId(),
                            fakeActivity.getTitle(),
                            fakeActivity.getThematic(),
                            fakeActivity.getSummary(),
                            fakeActivity.getDescription(),
                            fakeActivity.getExecutionPeriodInfo(),
                            Optional.ofNullable(fakeActivity.getStartDate()),
                            Optional.ofNullable(fakeActivity.getEndDate()),
                            new ActivityBannerCreationData(
                                banner.getFileName(), banner.getFileType(), banner.getSize()),
                            fakeActivity.isEnableReflection(),
                            new ArrayList<>(),
                            fakeActivity.getTraceAllowedAssociations(),
                            fakeActivity.getFeedbackAllowedIterations(),
                            now,
                            now);
                      })
                  .toList();
        };

    List<Activity> activities = new ArrayList<>();

    RequestContext.set(
        new RequestData(
            Optional.ofNullable(UserMapper.INSTANCE.toDomain(uploader.getUser())),
            ELanguage.FRENCH));

    creationData.forEach(
        data -> {
          try {
            if (data.updatedAt() != null) {
              clockService.fixed(data.updatedAt());
            } else {
              var now = clockService.now();
              clockService.fixed(now.minus(20, ChronoUnit.DAYS));
            }

            var activity =
                activityService.create(
                    data.id(),
                    StaffMapper.INSTANCE.toDomain(uploader),
                    data.title(),
                    data.thematic(),
                    data.summary(),
                    data.description(),
                    data.executionPeriodInfo(),
                    data.startDate().orElse(null),
                    data.endDate().orElse(null),
                    data.enableReflection(),
                    data.traceAllowedAssociations(),
                    data.feedbackAllowedIterations(),
                    data.links());

            activities.add(activity);
          } finally {
            clockService.clear();
          }
        });

    log.info("✔ {} activities created", activities.size());
    return activities.stream().map(ActivityMapper.INSTANCE::fromDomain).toList();
  }
}
