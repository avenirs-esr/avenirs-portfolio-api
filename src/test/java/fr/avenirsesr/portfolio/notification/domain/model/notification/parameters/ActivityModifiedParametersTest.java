package fr.avenirsesr.portfolio.notification.domain.model.notification.parameters;

import static fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField.ACTIVITY_TITLE;
import static fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField.DESCRIPTION;
import static fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField.SUB_ACTIVITIES;
import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import org.junit.jupiter.api.Test;

class ActivityModifiedParametersTest {

  @Test
  void toStringList_should_put_the_sub_activity_titles_after_the_updated_fields() {
    BddLogger.given("parameters holding updated fields and modified sub-activities");
    var parameters =
        new ActivityModifiedParameters(
            "Activité", List.of(SUB_ACTIVITIES, ACTIVITY_TITLE), List.of("Étape 1", "Étape 2"));

    BddLogger.when("toStringList is called");
    var values = parameters.toStringList();

    BddLogger.then("the sub-activity titles should follow the SUB_ACTIVITIES marker");
    assertThat(values)
        .containsExactly("Activité", "ACTIVITY_TITLE", "SUB_ACTIVITIES", "Étape 1", "Étape 2");
  }

  @Test
  void fromStringList_should_restore_the_updated_fields_and_the_sub_activity_titles() {
    BddLogger.given("parameters holding updated fields and modified sub-activities");
    var parameters =
        new ActivityModifiedParameters(
            "Activité", List.of(DESCRIPTION, SUB_ACTIVITIES), List.of("Étape 1", "Étape 2"));

    BddLogger.when("they are stored then read back");
    var restored = ActivityModifiedParameters.fromStringList(parameters.toStringList());

    BddLogger.then("they should be equal to the original ones");
    assertThat(restored).isEqualTo(parameters);
  }

  @Test
  void fromStringList_should_read_parameters_stored_without_sub_activities() {
    BddLogger.given("parameters stored before the sub-activities were tracked");
    var values = List.of("Activité", "ACTIVITY_TITLE", "DESCRIPTION");

    BddLogger.when("fromStringList is called");
    var parameters = ActivityModifiedParameters.fromStringList(values);

    BddLogger.then("the updated fields should be restored without any sub-activity title");
    assertThat(parameters.activityTitle()).isEqualTo("Activité");
    assertThat(parameters.updatedFields()).containsExactly(ACTIVITY_TITLE, DESCRIPTION);
    assertThat(parameters.updatedSubActivityTitles()).isEmpty();
  }

  @Test
  void fromStringList_should_keep_a_sub_activity_title_matching_a_field_name_as_a_title() {
    BddLogger.given("a modified sub-activity whose title matches the name of a field");
    var values = List.of("SUB_ACTIVITIES", "SUB_ACTIVITIES", "DESCRIPTION", "SUB_ACTIVITIES");

    BddLogger.when("fromStringList is called");
    var parameters = ActivityModifiedParameters.fromStringList(values);

    BddLogger.then("everything after the marker should be read as a sub-activity title");
    assertThat(parameters.activityTitle()).isEqualTo("SUB_ACTIVITIES");
    assertThat(parameters.updatedFields()).containsExactly(SUB_ACTIVITIES);
    assertThat(parameters.updatedSubActivityTitles())
        .containsExactly("DESCRIPTION", "SUB_ACTIVITIES");
  }
}
