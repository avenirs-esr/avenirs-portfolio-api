package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import java.util.List;

public record AssessmentRequestNotificationParameters(
    String studentFirstName, String studentLastName, String activityId, String activityTitle)
    implements NotificationParameters {

  public static AssessmentRequestNotificationParameters fromStringList(List<String> values) {
    return new AssessmentRequestNotificationParameters(
        values.get(0), values.get(1), values.get(2), values.get(3));
  }

  @Override
  public List<String> toStringList() {
    return List.of(studentFirstName, studentLastName, activityId, activityTitle);
  }
}
