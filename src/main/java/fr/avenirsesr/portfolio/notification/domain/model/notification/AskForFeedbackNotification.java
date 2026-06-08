package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import java.util.List;

public class AskForFeedbackNotification extends BaseNotification {

  private final Feedback feedback;

  public AskForFeedbackNotification(User user, Feedback feedback) {
    super(user, ENotificationType.ASK_FOR_FEEDBACK, feedback.getId());
    this.feedback = feedback;
  }

  @Override
  protected List<String> buildParameters() {
    return List.of(
        feedback.getDeclaredActivity().getStudent().getUser().getFirstName(),
        feedback.getDeclaredActivity().getStudent().getUser().getLastName(),
        feedback.getDeclaredActivity().getActivity().getTitle());
  }
}
