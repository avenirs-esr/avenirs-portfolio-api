package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.seeder.data.SubActivityCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class SubActivitySeeder {
  private static final String PATH_FILE = "seeder/sub-activities.json";

  private final FileReader fileReader;
  private final ActivityService activityService;
  private final ClockService clockService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public SubActivitySeeder(
      FileReader fileReader, ActivityService activityService, ClockService clockService) {
    this.fileReader = fileReader;
    this.activityService = activityService;
    this.clockService = clockService;
  }

  @Transactional
  public List<ActivityEntity> seed(StaffEntity uploader) {
    log.info("Seeding Sub-activities...");

    List<SubActivityCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<SubActivityCreationData>>() {});
          case FAKER -> List.of();
        };

    RequestContext.set(
        new RequestData(
            Optional.ofNullable(UserMapper.INSTANCE.toDomain(uploader.getUser())),
            ELanguage.FRENCH));

    List<Activity> subActivities = new ArrayList<>();
    creationData.forEach(
        data -> {
          try {
            if (data.updatedAt() != null) {
              clockService.fixed(data.updatedAt());
            }
            subActivities.add(
                activityService.createSubActivity(
                    data.id(),
                    data.previousActivityId(),
                    data.title(),
                    data.description(),
                    data.recommendedCompletionContexts(),
                    data.startDate().orElse(null),
                    data.endDate().orElse(null),
                    data.links()));
          } finally {
            clockService.clear();
          }
        });

    log.info("✔ {} sub-activities created", subActivities.size());
    return subActivities.stream().map(ActivityMapper.INSTANCE::fromDomain).toList();
  }
}
