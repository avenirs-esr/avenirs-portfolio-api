package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField;
import java.util.ArrayList;
import java.util.List;

public record ActivityModifiedParameters(
    String activityTitle, List<EActivityUpdatableField> updatedFields)
    implements NotificationParameters {

  public static ActivityModifiedParameters fromStringList(List<String> values) {
    return new ActivityModifiedParameters(
        values.getFirst(),
        values.subList(1, values.size()).stream().map(EActivityUpdatableField::valueOf).toList());
  }

  @Override
  public List<String> toStringList() {
    var result = new ArrayList<String>();
    result.add(activityTitle);
    updatedFields.forEach(field -> result.add(field.name()));
    return result;
  }
}
