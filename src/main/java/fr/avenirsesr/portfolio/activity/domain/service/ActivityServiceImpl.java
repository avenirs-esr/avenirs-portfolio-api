package fr.avenirsesr.portfolio.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotBlankAndMaxLength;
import static fr.avenirsesr.portfolio.common.validation.domain.utils.FieldValidationUtils.requireNotNull;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {
  private final ActivityRepository activityRepository;

  @Override
  public Activity create(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo) {
    requireNotBlankAndMaxLength("title", title, TITLE_LENGTH);
    requireNotNull("thematic", thematic);
    requireNotBlankAndMaxLength("summary", summary, DESCRIPTION_LENGTH);
    requireNotBlankAndMaxLength(
        "executionPeriodInfo", executionPeriodInfo, ACTIVITY_EXECUTION_PERIOD_INFO);

    var activity = Activity.create(id, title, thematic, summary, executionPeriodInfo);
    activityRepository.save(activity);
    return activity;
  }
}
