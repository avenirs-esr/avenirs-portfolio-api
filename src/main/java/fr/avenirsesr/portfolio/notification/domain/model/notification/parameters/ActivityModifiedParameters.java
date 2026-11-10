package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import static fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField.SUB_ACTIVITIES;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField;
import java.util.ArrayList;
import java.util.List;

public record ActivityModifiedParameters(
    String activityTitle,
    List<EActivityUpdatableField> updatedFields,
    List<String> updatedSubActivityTitles)
    implements NotificationParameters {

  public static ActivityModifiedParameters fromStringList(List<String> values) {
    var fieldsAndSubActivityTitles = values.subList(1, values.size());
    var subActivitiesIndex = fieldsAndSubActivityTitles.indexOf(SUB_ACTIVITIES.name());
    var fieldCount =
        subActivitiesIndex < 0 ? fieldsAndSubActivityTitles.size() : subActivitiesIndex + 1;
    return new ActivityModifiedParameters(
        values.getFirst(),
        fieldsAndSubActivityTitles.subList(0, fieldCount).stream()
            .map(EActivityUpdatableField::valueOf)
            .toList(),
        List.copyOf(
            fieldsAndSubActivityTitles.subList(fieldCount, fieldsAndSubActivityTitles.size())));
  }

  @Override
  public List<String> toStringList() {
    var result = new ArrayList<String>();
    result.add(activityTitle);
    updatedFields.stream()
        .filter(field -> field != SUB_ACTIVITIES)
        .forEach(field -> result.add(field.name()));
    if (updatedFields.contains(SUB_ACTIVITIES)) {
      result.add(SUB_ACTIVITIES.name());
      result.addAll(updatedSubActivityTitles);
    }
    return result;
  }
}
