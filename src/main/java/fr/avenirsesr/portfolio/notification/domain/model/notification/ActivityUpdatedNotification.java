package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityUpdatableField;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.ActivityModifiedParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import java.util.List;

public class ActivityUpdatedNotification extends BaseNotification {
  private final Activity activity;
  private final List<EActivityUpdatableField> updatedFields;

  public ActivityUpdatedNotification(
      User user, Activity activity, List<EActivityUpdatableField> updatedFields) {
    super(user, ENotificationType.ACTIVITY_MODIFIED, activity.getId());
    this.activity = activity;
    this.updatedFields = updatedFields;
  }

  @Override
  protected NotificationParameters buildParameters() {
    return new ActivityModifiedParameters(activity.getTitle(), updatedFields);
  }
}
