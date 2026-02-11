package fr.avenirsesr.portfolio.activity.domain.port.input;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.util.UUID;

public interface ActivityService {
  Activity create(
      UUID id,
      String title,
      EActivityThematic thematic,
      String summary,
      String executionPeriodInfo);
}
