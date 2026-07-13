package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.AskForFeedbackParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;

public class AskForFeedbackNotification extends BaseNotification {

  private final Feedback feedback;

  public AskForFeedbackNotification(User user, Feedback feedback) {
    super(user, ENotificationType.ASK_FOR_FEEDBACK, feedback.getId());
    this.feedback = feedback;
  }

  @Override
  protected NotificationParameters buildParameters() {
    return new AskForFeedbackParameters(
        feedback.getDeclaredActivity().getStudent().getUser().getFirstName(),
        feedback.getDeclaredActivity().getStudent().getUser().getLastName(),
        feedback.getDeclaredActivity().getActivity().getTitle());
  }
}
