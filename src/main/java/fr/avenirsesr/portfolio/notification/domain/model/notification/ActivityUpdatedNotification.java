package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.ActivityModifiedParameters;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.NotificationParameters;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import java.util.List;

public class ActivityUpdatedNotification extends BaseNotification {
  private final DeclaredActivity declaredActivity;
  private final List<EActivityUpdatableField> updatedFields;

  public ActivityUpdatedNotification(
      User user, DeclaredActivity declaredActivity, List<EActivityUpdatableField> updatedFields) {
    super(user, ENotificationType.ACTIVITY_MODIFIED, declaredActivity.getId());
    this.declaredActivity = declaredActivity;
    this.updatedFields = updatedFields;
  }

  @Override
  protected NotificationParameters buildParameters() {
    return new ActivityModifiedParameters(declaredActivity.getActivity().getTitle(), updatedFields);
  }
}
