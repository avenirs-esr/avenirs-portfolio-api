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
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeActivityBanner;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.io.IOException;
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
  private final ActivityResourceService activityResourceService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public ActivitySeeder(
      FileReader fileReader,
      ActivityService activityService,
      @Qualifier("MockActivityResourceService") ActivityResourceService activityResourceService) {
    this.fileReader = fileReader;
    this.activityService = activityService;
    this.activityResourceService = activityResourceService;
  }

  @Transactional
  public List<ActivityEntity> seed(UserEntity uploader) {
    log.info("Seeding Activities...");

    List<ActivityCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<ActivityCreationData>>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.NB_OF_ACTIVITIES)
                  .mapToObj(i -> FakeActivity.create().toEntity())
                  .map(
                      fakeActivity -> {
                        var banner = FakeActivityBanner.create(fakeActivity).toEntity();
                        return new ActivityCreationData(
                            fakeActivity.getId(),
                            fakeActivity.getTitle(),
                            fakeActivity.getThematic(),
                            fakeActivity.getSummary(),
                            fakeActivity.getExecutionPeriodInfo(),
                            Optional.ofNullable(fakeActivity.getExecutionPeriodInfoSummary()),
                            new ActivityBannerCreationData(
                                banner.getFileName(), banner.getFileType(), banner.getSize()));
                      })
                  .toList();
        };

    List<Activity> activities = new ArrayList<>();

    RequestContext.set(
        new RequestData(
            Optional.ofNullable(UserMapper.INSTANCE.toDomain(uploader)), ELanguage.FRENCH));
    creationData.forEach(
        data -> {
          var activity =
              activityService.create(
                  data.id(),
                  data.title(),
                  data.thematic(),
                  data.summary(),
                  data.executionPeriodInfo(),
                  data.executionPeriodInfoSummary().orElse(null));
          try {
            activityResourceService.uploadBannerFor(
                activity,
                data.banner().fileName(),
                data.banner().fileType().getMimeType(),
                data.banner().size(),
                null);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          activities.add(activity);
        });

    log.info("✔ {} activities created", activities.size());
    return activities.stream().map(ActivityMapper.INSTANCE::fromDomain).toList();
  }
}
