package fr.avenirsesr.portfolio.notification.domain.model.notification;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityUpdatableField;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import java.util.ArrayList;
import java.util.Collections;
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
  protected List<String> buildParameters() {
    var params = new ArrayList<>(Collections.singleton(activity.getTitle()));
    params.addAll(updatedFields.stream().map(Enum::name).toList());
    return params;
  }
}
