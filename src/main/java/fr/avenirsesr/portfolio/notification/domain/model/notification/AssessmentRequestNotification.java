package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.AssessmentRequestNotificationParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;

public class AssessmentRequestNotification extends BaseNotification {
  private final DeclaredActivity declaredActivity;

  public AssessmentRequestNotification(User user, DeclaredActivity declaredActivity) {
    super(user, ENotificationType.ASSESSMENT_REQUEST, declaredActivity.getId());

    this.declaredActivity = declaredActivity;
  }

  @Override
  protected NotificationParameters buildParameters() {
    return new AssessmentRequestNotificationParameters(
        declaredActivity.getStudent().getUser().getFirstName(),
        declaredActivity.getStudent().getUser().getLastName(),
        declaredActivity.getId().toString(),
        declaredActivity.getActivity().getTitle());
  }
}
