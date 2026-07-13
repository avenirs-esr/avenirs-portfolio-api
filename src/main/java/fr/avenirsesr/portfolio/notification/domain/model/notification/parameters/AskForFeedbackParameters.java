package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import java.util.List;

public record AskForFeedbackParameters(
    String studentFirstName, String studentLastName, String activityTitle)
    implements NotificationParameters {

  public static AskForFeedbackParameters fromStringList(List<String> values) {
    return new AskForFeedbackParameters(values.get(0), values.get(1), values.get(2));
  }

  @Override
  public List<String> toStringList() {
    return List.of(studentFirstName, studentLastName, activityTitle);
  }
}
