package fr.avenirsesr.portfolio.notification.domain.model.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AskForFeedbackNotificationTest {

  private User recipient;
  private Feedback feedback;
  private UUID feedbackId;

  @BeforeEach
  void setUp() {
    recipient = UserFixture.create().toModel();
    feedbackId = UUID.randomUUID();

    User studentUser = UserFixture.create().withFirstName("Alice").withLastName("Martin").toModel();
    Student student = mock(Student.class);
    when(student.getUser()).thenReturn(studentUser);

    Activity activity = mock(Activity.class);
    when(activity.getTitle()).thenReturn("Activité de test");

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getActivity()).thenReturn(activity);

    feedback = mock(Feedback.class);
    when(feedback.getId()).thenReturn(feedbackId);
    when(feedback.getDeclaredActivity()).thenReturn(declaredActivity);
  }

  @Test
  void build_should_create_notification_with_FEEDBACK_type() {
    BddLogger.given("A FeedbackNotification with a recipient and a feedback");
    AskForFeedbackNotification notif = new AskForFeedbackNotification(recipient, feedback);

    BddLogger.when("build() is called");
    Notification result = notif.build();

    BddLogger.then("The resulting Notification has type FEEDBACK");
    assertThat(result.getType()).isEqualTo(ENotificationType.ASK_FOR_FEEDBACK);
  }

  @Test
  void build_should_use_feedback_id_as_elementId() {
    BddLogger.given("A FeedbackNotification with a feedback whose id is known");
    AskForFeedbackNotification notif = new AskForFeedbackNotification(recipient, feedback);

    BddLogger.when("build() is called");
    Notification result = notif.build();

    BddLogger.then("The elementId of the Notification equals the feedback id");
    assertThat(result.getElementId()).isEqualTo(feedbackId);
  }

  @Test
  void build_should_assign_recipient_as_user() {
    BddLogger.given("A FeedbackNotification with a specific recipient user");
    AskForFeedbackNotification notif = new AskForFeedbackNotification(recipient, feedback);

    BddLogger.when("build() is called");
    Notification result = notif.build();

    BddLogger.then("The user of the Notification is the recipient");
    assertThat(result.getUser()).isEqualTo(recipient);
  }

  @Test
  void build_should_set_seen_to_false() {
    BddLogger.given("A freshly built FeedbackNotification");
    AskForFeedbackNotification notif = new AskForFeedbackNotification(recipient, feedback);

    BddLogger.when("build() is called");
    Notification result = notif.build();

    BddLogger.then("The notification is not yet seen");
    assertThat(result.isSeen()).isFalse();
  }

  @Test
  void buildParameters_should_contain_student_first_name_last_name_and_activity_title() {
    BddLogger.given(
        "A FeedbackNotification where student is 'Alice Martin' and activity is 'Activité de"
            + " test'");
    AskForFeedbackNotification notif = new AskForFeedbackNotification(recipient, feedback);

    BddLogger.when("build() is called");
    Notification result = notif.build();

    BddLogger.then("The parameters list contains the student full name and the activity title");
    assertThat(result.getParameters()).containsExactly("Alice", "Martin", "Activité de test");
  }
}
