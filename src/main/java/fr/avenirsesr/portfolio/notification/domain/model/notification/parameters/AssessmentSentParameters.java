package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import java.util.List;

public record AssessmentSentParameters(String activityTitle) implements NotificationParameters {

  public static AssessmentSentParameters fromStringList(List<String> values) {
    return new AssessmentSentParameters(values.getFirst());
  }

  @Override
  public List<String> toStringList() {
    return List.of(activityTitle);
  }
}
