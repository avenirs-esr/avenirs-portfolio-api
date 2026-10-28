package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import java.util.List;

public record AssessmentModifiedParameters(String activityTitle) implements NotificationParameters {

  public static AssessmentModifiedParameters fromStringList(List<String> values) {
    return new AssessmentModifiedParameters(values.getFirst());
  }

  @Override
  public List<String> toStringList() {
    return List.of(activityTitle);
  }
}
