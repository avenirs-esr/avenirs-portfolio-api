package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.ActivityModifiedParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;

public class ActivityUpdatedNotification extends BaseNotification {
  private final ActivityModifiedParameters parameters;

  public ActivityUpdatedNotification(
      User user, DeclaredActivity declaredActivity, ActivityModifiedParameters parameters) {
    super(user, ENotificationType.ACTIVITY_MODIFIED, declaredActivity.getId());
    this.parameters = parameters;
  }

  @Override
  protected NotificationParameters buildParameters() {
    return parameters;
  }
}
