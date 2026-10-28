package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.AssessmentModifiedParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;

public class AssessmentModifiedNotification extends BaseNotification {
  private final DeclaredActivity declaredActivity;

  public AssessmentModifiedNotification(User user, DeclaredActivity declaredActivity) {
    super(user, ENotificationType.ASSESSMENT_MODIFIED, declaredActivity.getId());
    this.declaredActivity = declaredActivity;
  }

  @Override
  protected NotificationParameters buildParameters() {
    return new AssessmentModifiedParameters(declaredActivity.getActivity().getTitle());
  }
}
