package fr.avenirsesr.portfolio.student.activity.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.AssessmentSentNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityAssessmentException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityUnpublishedException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityAuthorizationService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.activity.domain.exception.*;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivityAssessment;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.SkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityServiceImplTest {

  @Mock private DeclaredActivityRepository declaredActivityRepository;
  @Mock private ActivityService activityService;
  @Mock private AssociationService associationService;

  @Mock private LoggedInUserService loggedInUserService;
  @Mock private FeedbackRepository feedbackRepository;
  @Mock private FeedbackService feedbackService;
  @Mock private NotificationService notificationService;
  @Mock private ActivityAuthorizationService activityAuthorizationService;
  @Mock private TraceService traceService;
  @Mock private SkillProgressService skillProgressService;
  @Mock private DeclaredExperienceService declaredExperienceService;
  @InjectMocks private DeclaredActivityServiceImpl service;

  private DeclaredActivityService declaredActivityService;

  @Captor private ArgumentCaptor<DeclaredActivity> activityCaptor;

  private Student student;
  private final UUID declaredActivityId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    declaredActivityService =
        new DeclaredActivityServiceImpl(
            declaredActivityRepository,
            activityService,
            associationService,
            loggedInUserService,
            feedbackRepository,
            feedbackService,
            notificationService,
            activityAuthorizationService,
            traceService,
            skillProgressService,
            declaredExperienceService);
  }

  @Test
  void sendAssessment_should_send_the_assessment_and_notify_the_student() {
    BddLogger.given("An evaluator and a declared activity whose activity expects a graded comment");
    Staff staff = StaffFixture.create().toModel();
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, 20, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment");
    service.sendAssessment(declaredActivityId, "Bon travail", 15, null);

    BddLogger.then(
        "The assessment is set, the declared activity finished and the student notified");
    verify(activityAuthorizationService)
        .checkRight(staff, activity.getId(), EActivityRight.EVALUATION);
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(
            sent -> {
              assertThat(sent.comment()).isEqualTo("Bon travail");
              assertThat(sent.grade()).isEqualTo(15);
              assertThat(sent.validation()).isNull();
              assertThat(sent.assessedAt()).isNotNull();
              assertThat(sent.assessedBy()).isEqualTo(staff);
              assertThat(sent.modifiedAt()).isNull();
            });
    assertThat(activityCaptor.getValue().getFinishedAt()).isPresent();

    ArgumentCaptor<AssessmentSentNotification> notificationCaptor = ArgumentCaptor.captor();
    verify(notificationService).notify(notificationCaptor.capture());

    var notification = notificationCaptor.getValue().build();
    assertThat(notification.getType()).isEqualTo(ENotificationType.ASSESSMENT_SENT);
    assertThat(notification.getUser()).isEqualTo(student.getUser());
    assertThat(notification.getElementId()).isEqualTo(declaredActivityId);
  }

  @Test
  void sendAssessment_should_keep_the_date_at_which_the_assessment_was_requested() {
    BddLogger.given("An evaluator and a declared activity submitted for assessment a day ago");
    Instant requestedAt = Instant.now().minus(Duration.ofDays(1));
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, null, null)).toModel();
    DeclaredActivity declaredActivity =
        declaredActivityWith(activity, DeclaredActivityAssessment.requested(requestedAt));

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment");
    service.sendAssessment(declaredActivityId, "Bon travail", null, null);

    BddLogger.then("The assessment keeps its request date");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(sent -> assertThat(sent.requestedAt()).isEqualTo(requestedAt));
  }

  @Test
  void sendAssessment_should_keep_the_date_at_which_the_declared_activity_was_finished() {
    BddLogger.given("An evaluator and a declared activity finished a week ago");
    Instant finishedAt = Instant.now().minus(Duration.ofDays(7));
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, null, null)).toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.toDomain(
            declaredActivityId,
            student,
            activity,
            null,
            null,
            null,
            null,
            finishedAt,
            Instant.now(),
            false,
            null,
            null,
            Instant.now(),
            Instant.now());

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment");
    service.sendAssessment(declaredActivityId, "Bon travail", null, null);

    BddLogger.then("The declared activity keeps its own completion date");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getFinishedAt()).contains(finishedAt);
  }

  @Test
  void sendAssessment_should_accept_a_grade_of_zero() {
    BddLogger.given("An evaluator and a declared activity graded out of 20");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(null, 20, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment graded zero");
    service.sendAssessment(declaredActivityId, null, 0, null);

    BddLogger.then("The assessment is set");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(sent -> assertThat(sent.grade()).isZero());
  }

  @Test
  void sendAssessment_should_accept_a_grade_without_comment_when_the_comment_is_optional() {
    BddLogger.given("An evaluator and a declared activity whose activity expects a graded comment");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, 20, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment holding a grade only");
    service.sendAssessment(declaredActivityId, null, 15, null);

    BddLogger.then("The assessment is set without comment and the student notified");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(
            sent -> {
              assertThat(sent.comment()).isNull();
              assertThat(sent.grade()).isEqualTo(15);
            });
    verify(notificationService).notify(any());
  }

  @Test
  void sendAssessment_should_accept_a_validation_without_comment_when_the_comment_is_optional() {
    BddLogger.given(
        "An evaluator and a declared activity whose activity expects a validation and a comment");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, null, true)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment holding a validation only");
    service.sendAssessment(declaredActivityId, null, null, false);

    BddLogger.then("The assessment is set without comment and the student notified");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(
            sent -> {
              assertThat(sent.comment()).isNull();
              assertThat(sent.validation()).isFalse();
            });
    verify(notificationService).notify(any());
  }

  @Test
  void sendAssessment_should_throw_FieldValidationException_when_the_comment_is_missing() {
    BddLogger.given("An evaluator and a declared activity whose activity expects a comment only");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, null, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment without comment");

    BddLogger.then("A FieldValidationException is thrown and nothing is sent");
    FieldValidationException exception =
        Assertions.assertThrows(
            FieldValidationException.class,
            () -> service.sendAssessment(declaredActivityId, null, null, null));

    Assertions.assertEquals(EErrorCode.NOT_BLANK, exception.getErrorCode());
    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_throw_FieldValidationException_when_the_grade_is_missing() {
    BddLogger.given("An evaluator and a declared activity whose activity expects a grade");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(null, 20, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment without grade");

    BddLogger.then("A FieldValidationException is thrown and nothing is sent");
    FieldValidationException exception =
        Assertions.assertThrows(
            FieldValidationException.class,
            () -> service.sendAssessment(declaredActivityId, null, null, null));

    Assertions.assertEquals(EErrorCode.NOT_NULL, exception.getErrorCode());
    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_throw_ActivityAssessmentException_when_a_field_is_disabled() {
    BddLogger.given("An evaluator and a declared activity expecting a comment only");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, null, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment holding a grade");

    BddLogger.then("An ActivityAssessmentException is thrown and nothing is sent");
    assertThatThrownBy(() -> service.sendAssessment(declaredActivityId, "Bon travail", 15, null))
        .isInstanceOf(ActivityAssessmentException.class);

    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_throw_ActivityAssessmentException_when_the_grade_is_out_of_range() {
    BddLogger.given("An evaluator and a declared activity graded out of 10");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(null, 10, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment graded above the scale");

    BddLogger.then("An ActivityAssessmentException is thrown and nothing is sent");
    assertThatThrownBy(() -> service.sendAssessment(declaredActivityId, null, 11, null))
        .isInstanceOf(ActivityAssessmentException.class);

    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_throw_ActivityAssessmentException_when_no_assessment_is_expected() {
    BddLogger.given("An evaluator and a declared activity whose activity expects no assessment");
    DeclaredActivity declaredActivity =
        declaredActivityWith(ActivityFixture.create().toModel(), null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends an assessment");

    BddLogger.then("An ActivityAssessmentException is thrown and nothing is sent");
    assertThatThrownBy(() -> service.sendAssessment(declaredActivityId, "Bon travail", null, null))
        .isInstanceOf(ActivityAssessmentException.class);

    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_overwrite_a_sent_assessment_without_notifying_the_student() {
    BddLogger.given("An evaluator and a declared activity whose assessment was sent and consulted");
    Instant requestedAt = Instant.now().minus(Duration.ofDays(3));
    Instant sentAt = Instant.now().minus(Duration.ofDays(2));
    Instant consultedAt = Instant.now().minus(Duration.ofDays(1));
    Staff staff = StaffFixture.create().toModel();
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, 20, null)).toModel();
    DeclaredActivity declaredActivity =
        declaredActivityWith(
            activity,
            DeclaredActivityAssessment.requested(requestedAt)
                .send("Bon travail", 15, null, sentAt, StaffFixture.create().toModel())
                .consult(consultedAt));

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends another version of the assessment");
    service.sendAssessment(declaredActivityId, "Très bon travail", 17, null);

    BddLogger.then("The assessment is overwritten and the student is not notified again");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(
            modified -> {
              assertThat(modified.comment()).isEqualTo("Très bon travail");
              assertThat(modified.grade()).isEqualTo(17);
              assertThat(modified.requestedAt()).isEqualTo(requestedAt);
              assertThat(modified.assessedAt()).isEqualTo(sentAt);
              assertThat(modified.assessedBy()).isEqualTo(staff);
              assertThat(modified.modifiedAt()).isAfter(sentAt);
              assertThat(modified.consultedAt()).isEqualTo(consultedAt);
            });
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_validate_the_modification_of_a_sent_assessment() {
    BddLogger.given("An evaluator and a declared activity whose assessment was sent");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, null, null)).toModel();
    DeclaredActivity declaredActivity =
        declaredActivityWith(
            activity,
            DeclaredActivityAssessment.requested(null)
                .send("Bon travail", null, null, Instant.now(), StaffFixture.create().toModel()));

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He sends another version without comment");

    BddLogger.then("A FieldValidationException is thrown and nothing is saved");
    FieldValidationException exception =
        Assertions.assertThrows(
            FieldValidationException.class,
            () -> service.sendAssessment(declaredActivityId, null, null, null));

    Assertions.assertEquals(EErrorCode.NOT_BLANK, exception.getErrorCode());
    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_throw_UserNotAuthorizedException_without_the_evaluation_right() {
    BddLogger.given("A staff member without the evaluation right on the activity");
    Staff staff = StaffFixture.create().toModel();
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, null, null)).toModel();
    DeclaredActivity declaredActivity = declaredActivityWith(activity, null);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    doThrow(new UserNotAuthorizedException())
        .when(activityAuthorizationService)
        .checkRight(staff, activity.getId(), EActivityRight.EVALUATION);

    BddLogger.when("He sends an assessment");

    BddLogger.then("A UserNotAuthorizedException is thrown and nothing is sent");
    assertThatThrownBy(() -> service.sendAssessment(declaredActivityId, "Bon travail", null, null))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  @Test
  void sendAssessment_should_throw_DeclaredActivityNotFoundException_when_it_does_not_exist() {
    BddLogger.given("An evaluator and an unknown declared activity");
    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(declaredActivityId)).thenReturn(Optional.empty());

    BddLogger.when("He sends an assessment");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown and nothing is sent");
    assertThatThrownBy(() -> service.sendAssessment(declaredActivityId, "Bon travail", null, null))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).save(any());
    verify(notificationService, never()).notify(any());
  }

  private DeclaredActivity declaredActivityWith(
      Activity activity, DeclaredActivityAssessment assessment) {
    return DeclaredActivity.toDomain(
        declaredActivityId,
        student,
        activity,
        null,
        null,
        null,
        null,
        null,
        Instant.now(),
        false,
        null,
        assessment,
        Instant.now(),
        Instant.now());
  }

  @Test
  void subscribe_should_create_and_save_when_activity_exists_and_not_already_subscribed() {
    BddLogger.given("A logged-in student and an existing activity he is not subscribed to");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findByActivity(student, activity)).thenReturn(Optional.empty());
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe to this activity without dates");
    service.subscribe(activityId, null, null);

    BddLogger.then("The student subscribed to the activity");
    verify(declaredActivityRepository).save(activityCaptor.capture());
    DeclaredActivity savedActivity = activityCaptor.getValue();

    assertThat(savedActivity.getStudent()).isEqualTo(student);
    assertThat(savedActivity.getActivity()).isEqualTo(activity);
    assertThat(savedActivity.getStartedAt()).isEmpty();
    assertThat(savedActivity.getStartDate()).isNull();
    assertThat(savedActivity.getEndDate()).isNull();
  }

  @Test
  void subscribe_should_throw_ActivityNotFoundException_when_activity_does_not_exist() {
    BddLogger.given("A logged-in student and a non-existent activity ID");
    UUID activityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenThrow(new ActivityNotFoundException());

    BddLogger.when("He tries to subscribe to this activity");

    BddLogger.then("An ActivityNotFoundException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.subscribe(activityId, null, null))
        .isInstanceOf(ActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).findByActivity(any(), any());
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_throw_ActivityUnpublishedException_when_activity_is_unpublished() {
    BddLogger.given("A logged-in student and an unpublished activity");
    Activity activity = mock(Activity.class);
    UUID activityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenReturn(activity);
    when(activity.getStatus()).thenReturn(EActivityStatus.UNPUBLISHED);

    BddLogger.when("He tries to subscribe to the unpublished activity");

    BddLogger.then("An ActivityUnpublishedException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.subscribe(activityId, null, null))
        .isInstanceOf(ActivityUnpublishedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_throw_DeclaredActivityAlreadyExistException_when_already_subscribed() {
    BddLogger.given("A logged-in student already subscribed to an existing activity");
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activity.getId())).thenReturn(activity);
    when(declaredActivityRepository.findByActivity(student, activity))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to subscribe to it again");

    BddLogger.then("A DeclaredActivityAlreadyExistException is thrown");
    assertThatThrownBy(() -> service.subscribe(activity.getId(), null, null))
        .isInstanceOf(DeclaredActivityAlreadyExistException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_reuse_the_existing_declared_activity_when_student_was_unsubscribed() {
    BddLogger.given("A logged-in student who unsubscribed from an activity");
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), student, activity, Instant.now(), "my reflection", null, null, null);
    declaredActivity.unsubscribe(Instant.now());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activity.getId())).thenReturn(activity);
    when(declaredActivityRepository.findByActivity(student, activity))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivityRepository.save(any(DeclaredActivity.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("He subscribes to this activity again");
    DeclaredActivity result = service.subscribe(activity.getId(), null, null);

    BddLogger.then("The former declared activity is reused and no longer unsubscribed");
    assertThat(result).isSameAs(declaredActivity);
    assertThat(result.isUnsubscribed()).isFalse();
    assertThat(result.getReflection()).isEqualTo("my reflection");
    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void subscribe_should_apply_the_new_period_when_resubscribing_with_dates() {
    BddLogger.given("A logged-in student who unsubscribed from an activity");
    Activity activity = ActivityFixture.create().toModel();
    LocalDate startDate = LocalDate.now().plusDays(2);
    LocalDate endDate = LocalDate.now().plusDays(5);
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            activity,
            null,
            null,
            LocalDate.now().plusDays(20),
            LocalDate.now().plusDays(30),
            null);
    declaredActivity.unsubscribe(Instant.now());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activity.getId())).thenReturn(activity);
    when(declaredActivityRepository.findByActivity(student, activity))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivityRepository.save(any(DeclaredActivity.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("He subscribes again with a new period");
    DeclaredActivity result = service.subscribe(activity.getId(), startDate, endDate);

    BddLogger.then("The period is updated on the reused declared activity");
    assertThat(result.getStartDate()).isEqualTo(startDate);
    assertThat(result.getEndDate()).isEqualTo(endDate);
    assertThat(result.isUnsubscribed()).isFalse();
  }

  @Test
  void subscribe_should_throw_DeclaredActivityDatesException_when_only_one_date_provided() {
    BddLogger.given("A logged-in student and a valid activity, but only startDate is provided");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    LocalDate startDate = LocalDate.now();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe with incomplete dates");

    BddLogger.then("A DeclaredActivityDatesException is thrown");
    assertThatThrownBy(() -> service.subscribe(activityId, startDate, null))
        .isInstanceOf(DeclaredActivityDatesException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_throw_FieldValidationException_when_endDate_before_startDate() {
    BddLogger.given("A logged-in student and dates where endDate is before startDate");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    LocalDate startDate = LocalDate.now().plusDays(5);
    LocalDate endDate = LocalDate.now().plusDays(2);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe with inconsistent dates");

    BddLogger.then("A FieldValidationException is thrown");
    assertThatThrownBy(() -> service.subscribe(activityId, startDate, endDate))
        .isInstanceOf(FieldValidationException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void
      subscribe_should_throw_DeclaredActivityStartDateBeforeSubscriptionException_when_startDate_is_in_past() {
    BddLogger.given("A logged-in student and a startDate that is before today");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    LocalDate startDate = LocalDate.now().minusDays(10);
    LocalDate endDate = LocalDate.now().plusDays(5);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe with a past startDate");

    BddLogger.then("A DeclaredActivityStartDateBeforeSubscriptionException is thrown");
    assertThatThrownBy(() -> service.subscribe(activityId, startDate, endDate))
        .isInstanceOf(DeclaredActivityStartDateBeforeSubscriptionException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void
      unsubscribeMultiple_shouldMarkDeclaredActivitiesAsUnsubscribed_whenOwnedByStudent_and_all_activityIds_found() {
    BddLogger.given("Valid declared activities (found by activityIds) owned by the student");

    var activity1 = ActivityFixture.create().toModel();
    var activity2 = ActivityFixture.create().toModel();
    var activityIds = List.of(activity1.getId(), activity2.getId());

    var declaredActivity1 =
        DeclaredActivity.create(
            UUID.randomUUID(), student, activity1, null, null, null, null, null);
    var declaredActivity2 =
        DeclaredActivity.create(
            UUID.randomUUID(), student, activity2, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findAllByActivityIdAndStudent(
            eq(activityIds), eq(student), any(FetchGraph.class)))
        .thenReturn(List.of(declaredActivity1, declaredActivity2));

    BddLogger.when("He requests to unsubscribe from these activities");
    declaredActivityService.unsubscribeMultiple(activityIds);

    BddLogger.then("The declared activities are kept but flagged as unsubscribed");
    assertThat(declaredActivity1.isUnsubscribed()).isTrue();
    assertThat(declaredActivity2.isUnsubscribed()).isTrue();
    verify(declaredActivityRepository).saveAll(List.of(declaredActivity1, declaredActivity2));
    verify(declaredActivityRepository, never()).removeAllFromDatabase(anyList());

    BddLogger.and("Their pending feedbacks are deleted");
    verify(feedbackService)
        .deletePendingFeedbacks(List.of(declaredActivity1.getId(), declaredActivity2.getId()));
  }

  @Test
  void unsubscribeMultiple_should_ignore_already_unsubscribed_declared_activities() {
    BddLogger.given("A declared activity the student already unsubscribed from");

    var activity = ActivityFixture.create().toModel();
    var activityIds = List.of(activity.getId());
    var unsubscribedAt = Instant.now().minusSeconds(3600);
    var declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);
    declaredActivity.unsubscribe(unsubscribedAt);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findAllByActivityIdAndStudent(
            eq(activityIds), eq(student), any(FetchGraph.class)))
        .thenReturn(List.of(declaredActivity));

    BddLogger.when("He requests to unsubscribe from it again");
    declaredActivityService.unsubscribeMultiple(activityIds);

    BddLogger.then("Nothing is changed and no feedback is deleted");
    assertThat(declaredActivity.getUnsubscribedAt()).contains(unsubscribedAt);
    verify(declaredActivityRepository, never()).saveAll(anyList());
    verify(feedbackService, never()).deletePendingFeedbacks(anyList());
  }

  @Test
  void
      unsubscribeMultiple_shouldThrowDeclaredActivityNotFoundException_when_some_activityIds_not_found_for_student() {
    BddLogger.given("Some activityIds are not subscribed by the student");

    var declaredActivity1 = mock(DeclaredActivity.class);
    var activity1 = mock(Activity.class);

    var idActivity1 = UUID.randomUUID();
    var idActivity2Missing = UUID.randomUUID();
    var activityIds = List.of(idActivity1, idActivity2Missing);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findAllByActivityIdAndStudent(
            eq(activityIds), eq(student), any(FetchGraph.class)))
        .thenReturn(List.of(declaredActivity1));

    when(declaredActivity1.getActivity()).thenReturn(activity1);
    when(activity1.getId()).thenReturn(idActivity1);

    BddLogger.when("He requests to unsubscribe from these activities");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown and nothing is unsubscribed");
    assertThatThrownBy(() -> declaredActivityService.unsubscribeMultiple(activityIds))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).saveAll(anyList());
  }

  @Test
  void finish_should_update_finishedAt_and_save_when_valid() {
    BddLogger.given("A logged-in student and his existing started declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), student, activity, Instant.now(), null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivityRepository.save(any(DeclaredActivity.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("He tries to finish this activity");
    service.finish(declaredActivityId);

    BddLogger.then("The declared activity is marked as finished and saved");
    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void finish_should_throw_DeclaredActivityHasNotStartedException_when_not_started() {
    BddLogger.given("A logged-in student and a declared activity that has not started yet");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to finish this activity");

    BddLogger.then("A DeclaredActivityHasNotStartedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityHasNotStartedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void finish_should_throw_DeclaredActivityNotFoundException_when_not_found() {
    BddLogger.given("A logged-in student and a non-existent declared activity ID");
    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId)).thenReturn(Optional.empty());

    BddLogger.when("He tries to finish this activity");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void finish_should_throw_UserNotAuthorizedException_when_belonging_to_another_student() {
    BddLogger.given("A logged-in student and a declared activity belonging to another student");
    UUID declaredActivityId = UUID.randomUUID();

    Student anotherStudent = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();

    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), anotherStudent, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to finish this activity");

    BddLogger.then("A UserNotAuthorizedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void finish_should_throw_DeclaredActivityAlreadyFinishedException_when_already_finished() {
    BddLogger.given("A logged-in student and his already finished declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    declaredActivity.setStartedAt(Instant.now());
    declaredActivity.setFinishedAt(Instant.now());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to finish this activity again");

    BddLogger.then("A DeclaredActivityAlreadyFinishedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityAlreadyFinishedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void submitForAssessment_should_set_submittedAt_and_save_when_the_activity_is_assessable() {
    BddLogger.given("A logged-in student and his declared activity of an assessable activity");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = assessableDeclaredActivity();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivityRepository.save(any(DeclaredActivity.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("He submits it for assessment");
    service.submitForAssessment(declaredActivityId);

    BddLogger.then("The submission date is set and the declared activity is saved");
    verify(declaredActivityRepository).save(activityCaptor.capture());
    assertThat(activityCaptor.getValue().getSubmittedAt()).isPresent();
  }

  @Test
  void submitForAssessment_should_record_the_assessment_request_date() {
    BddLogger.given("A logged-in student and his declared activity of an assessable activity");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = assessableDeclaredActivity();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivityRepository.save(any(DeclaredActivity.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("He submits it for assessment");
    service.submitForAssessment(declaredActivityId);

    BddLogger.then("The assessment holds the request date and waits for the assessor");
    verify(declaredActivityRepository).save(activityCaptor.capture());
    DeclaredActivity saved = activityCaptor.getValue();
    assertThat(saved.getAssessment())
        .get()
        .satisfies(
            assessment -> {
              assertThat(assessment.requestedAt()).isEqualTo(saved.getSubmittedAt().orElseThrow());
              assertThat(assessment.assessedAt()).isNull();
              assertThat(assessment.assessedBy()).isNull();
            });
  }

  @Test
  void submitForAssessment_should_make_the_declared_activity_SUBMITTED() {
    BddLogger.given("A declared activity submitted for assessment");
    DeclaredActivity declaredActivity = assessableDeclaredActivity();
    declaredActivity.setSubmittedAt(Instant.now());

    when(feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(
            List.of(declaredActivity.getId())))
        .thenReturn(List.of());

    BddLogger.when("Its status is computed");
    BddLogger.then("It is SUBMITTED");
    assertThat(service.getDeclaredActivityStatus(declaredActivity))
        .isEqualTo(EDeclaredActivityStatus.SUBMITTED);
  }

  @Test
  void submitForAssessment_should_throw_ActivityAssessmentException_when_not_assessable() {
    BddLogger.given("A logged-in student and his declared activity of a non assessable activity");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            ActivityFixture.create().toModel(),
            Instant.now(),
            null,
            null,
            null,
            null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He submits it for assessment");

    BddLogger.then("An ActivityAssessmentException is thrown");
    assertThatThrownBy(() -> service.submitForAssessment(declaredActivityId))
        .isInstanceOf(ActivityAssessmentException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void submitForAssessment_should_throw_ActivityAssessmentException_when_assessment_is_empty() {
    BddLogger.given("A declared activity of an activity whose assessment enables nothing");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity =
        assessableDeclaredActivity(new ActivityAssessment(false, null, false));

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He submits it for assessment");

    BddLogger.then("An ActivityAssessmentException is thrown");
    assertThatThrownBy(() -> service.submitForAssessment(declaredActivityId))
        .isInstanceOf(ActivityAssessmentException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void submitForAssessment_should_throw_UserNotAuthorizedException_when_belonging_to_another() {
    BddLogger.given("A declared activity belonging to another student");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            StudentFixture.create().toModel(),
            ActivityFixture.create()
                .withAssessment(new ActivityAssessment(true, null, null))
                .toModel(),
            Instant.now(),
            null,
            null,
            null,
            null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He submits it for assessment");

    BddLogger.then("A UserNotAuthorizedException is thrown");
    assertThatThrownBy(() -> service.submitForAssessment(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void submitForAssessment_should_throw_DeclaredActivityNotFoundException_when_not_found() {
    BddLogger.given("A non-existent declared activity id");
    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("He submits it for assessment");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.submitForAssessment(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void submitForAssessment_should_throw_DeclaredActivityLockedException_when_already_submitted() {
    BddLogger.given("A declared activity already submitted for assessment");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = assessableDeclaredActivity();
    declaredActivity.setSubmittedAt(Instant.now());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He submits it for assessment again");

    BddLogger.then("A DeclaredActivityLockedException is thrown");
    assertThatThrownBy(() -> service.submitForAssessment(declaredActivityId))
        .isInstanceOf(DeclaredActivityLockedException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void submitForAssessment_should_throw_DeclaredActivityUnsubscribedException_when_unsubscribed() {
    BddLogger.given("A declared activity the student unsubscribed from");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = assessableDeclaredActivity();
    declaredActivity.unsubscribe(Instant.now());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He submits it for assessment");

    BddLogger.then("A DeclaredActivityUnsubscribedException is thrown");
    assertThatThrownBy(() -> service.submitForAssessment(declaredActivityId))
        .isInstanceOf(DeclaredActivityUnsubscribedException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void getAssessmentDetails_should_return_the_submission_when_the_staff_can_evaluate() {
    BddLogger.given("A submitted declared activity and a staff allowed to evaluate it");
    UUID declaredActivityId = UUID.randomUUID();
    Staff staff = StaffFixture.create().toModel();
    DeclaredActivity declaredActivity = assessableDeclaredActivity();
    declaredActivity.setSubmittedAt(Instant.now());

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He gets the assessment details");
    var details = service.getAssessmentDetails(declaredActivityId);

    BddLogger.then("The submission, its associations and its status are returned");
    assertThat(details.declaredActivity()).isEqualTo(declaredActivity);
    assertThat(details.status()).isEqualTo(EDeclaredActivityStatus.SUBMITTED);
    assertThat(details.associations().traces()).isEmpty();
    assertThat(details.associations().skillProgresses()).isEmpty();
    assertThat(details.associations().declaredExperiences()).isEmpty();
    verify(activityAuthorizationService)
        .checkRight(staff, declaredActivity.getActivity().getId(), EActivityRight.EVALUATION);
  }

  @Test
  void getAssessmentDetails_should_throw_UserNotAuthorizedException_without_evaluation_right() {
    BddLogger.given("A submitted declared activity of an activity the staff cannot evaluate");
    UUID declaredActivityId = UUID.randomUUID();
    Staff staff = StaffFixture.create().toModel();
    DeclaredActivity declaredActivity = assessableDeclaredActivity();
    declaredActivity.setSubmittedAt(Instant.now());

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    doThrow(new UserNotAuthorizedException())
        .when(activityAuthorizationService)
        .checkRight(staff, declaredActivity.getActivity().getId(), EActivityRight.EVALUATION);

    BddLogger.when("He gets the assessment details");

    BddLogger.then("A UserNotAuthorizedException is thrown");
    assertThatThrownBy(() -> service.getAssessmentDetails(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void getAssessmentDetails_should_throw_DeclaredActivityNotFoundException_when_not_submitted() {
    BddLogger.given("A declared activity that was not submitted for assessment");
    UUID declaredActivityId = UUID.randomUUID();
    Staff staff = StaffFixture.create().toModel();
    DeclaredActivity declaredActivity = assessableDeclaredActivity();

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("The staff gets the assessment details");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.getAssessmentDetails(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
  }

  @Test
  void getAssessmentDetails_should_throw_DeclaredActivityNotFoundException_when_not_found() {
    BddLogger.given("A non-existent declared activity id");
    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStaff()).thenReturn(StaffFixture.create().toModel());
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("The staff gets the assessment details");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.getAssessmentDetails(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
  }

  @Test
  void shouldUpdateReflectionSuccessfully() {
    BddLogger.given("Un DeclaredActivity existant récupéré depuis le repository");

    UUID activityId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    Activity activity = ActivityFixture.create().toModel();
    Student student = mock(Student.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getActivity()).thenReturn(activity);
    when(declaredActivity.getStartedAt()).thenReturn(Optional.empty());

    when(declaredActivityRepository.findById(activityId)).thenReturn(Optional.of(declaredActivity));

    String reflection = "Nouvelle réflexion";
    BddLogger.and("Un body reflection contenant : " + reflection);

    BddLogger.when("Le service updateReflection est appelé");
    declaredActivityService.updateReflection(activityId, reflection);

    BddLogger.then("La reflection doit être mise à jour");
    verify(declaredActivity).setReflection(reflection);

    BddLogger.and("La DeclaredActivity doit être démarrée");
    verify(declaredActivity).setStartedAt(any(Instant.class));

    BddLogger.and("La DeclaredActivity doit être persistée via le repository");
    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void getDeclaredActivityDetails_should_return_declared_activity_when_owned_by_student() {
    BddLogger.given("A logged-in student and an existing declared activity belonging to him");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId)).thenReturn(List.of());

    BddLogger.when("He requests declared activity details");
    DeclaredActivityDetailsData result = service.getDeclaredActivityDetails(declaredActivityId);

    BddLogger.then("The declared activity is returned with an empty feedbacks list");
    assertThat(result.declaredActivity()).isSameAs(declaredActivity);
    assertThat(result.feedbacks()).isEmpty();
    verify(declaredActivityRepository).findById(eq(declaredActivityId), any(FetchGraph.class));
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void getDeclaredActivityDetails_should_include_feedbacks_mapped_as_FeedbackData() {
    BddLogger.given(
        "A logged-in student, his declared activity, and 2 feedbacks in the repository");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    Feedback feedback1 = mock(Feedback.class);
    Feedback feedback2 = mock(Feedback.class);
    FeedbackData feedbackData1 = mock(FeedbackData.class);
    FeedbackData feedbackData2 = mock(FeedbackData.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
        .thenReturn(List.of(feedback1, feedback2));
    when(feedbackService.getStudentFeedbackDetails(student.getUser(), feedback1))
        .thenReturn(feedbackData1);
    when(feedbackService.getStudentFeedbackDetails(student.getUser(), feedback2))
        .thenReturn(feedbackData2);

    BddLogger.when("getDeclaredActivityDetails is called");
    DeclaredActivityDetailsData result = service.getDeclaredActivityDetails(declaredActivityId);

    BddLogger.then(
        "The result contains the declared activity and 2 FeedbackData mapped via"
            + " getStudentFeedbackDetails");
    assertThat(result.declaredActivity()).isSameAs(declaredActivity);
    assertThat(result.feedbacks()).containsExactly(feedbackData1, feedbackData2);
    verify(feedbackRepository).findAllByDeclaredActivityId(declaredActivityId);
    verify(feedbackService).getStudentFeedbackDetails(student.getUser(), feedback1);
    verify(feedbackService).getStudentFeedbackDetails(student.getUser(), feedback2);
  }

  @Test
  void getDeclaredActivityDetails_should_return_empty_feedbacks_when_none_exist() {
    BddLogger.given("A logged-in student and his declared activity with no feedbacks yet");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId)).thenReturn(List.of());

    BddLogger.when("getDeclaredActivityDetails is called");
    DeclaredActivityDetailsData result = service.getDeclaredActivityDetails(declaredActivityId);

    BddLogger.then("The result contains the declared activity and an empty feedbacks list");
    assertThat(result.declaredActivity()).isSameAs(declaredActivity);
    assertThat(result.feedbacks()).isEmpty();
    verify(feedbackRepository).findAllByDeclaredActivityId(declaredActivityId);
    verifyNoInteractions(feedbackService);
  }

  @Test
  void getDeclaredActivityDetails_should_set_the_consultation_date_of_a_sent_assessment() {
    BddLogger.given("A logged-in student and his declared activity holding a sent assessment");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, 20, null)).toModel();
    DeclaredActivity declaredActivity =
        declaredActivityWith(
            activity,
            DeclaredActivityAssessment.requested(null)
                .send("Bon travail", 15, null, Instant.now(), StaffFixture.create().toModel()));

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId)).thenReturn(List.of());

    BddLogger.when("He requests the details of that declared activity");
    service.getDeclaredActivityDetails(declaredActivityId);

    BddLogger.then("The assessment keeps its content and gets a consultation date");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(
            consulted -> {
              assertThat(consulted.comment()).isEqualTo("Bon travail");
              assertThat(consulted.grade()).isEqualTo(15);
              assertThat(consulted.consultedAt()).isNotNull();
            });
  }

  @Test
  void getDeclaredActivityDetails_should_update_the_consultation_date_on_each_read() {
    BddLogger.given("A logged-in student and his declared activity consulted a week ago");
    Instant lastConsultedAt = Instant.now().minus(Duration.ofDays(7));
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, 20, null)).toModel();
    DeclaredActivity declaredActivity =
        declaredActivityWith(
            activity,
            DeclaredActivityAssessment.requested(null)
                .send("Bon travail", 15, null, Instant.now(), StaffFixture.create().toModel())
                .consult(lastConsultedAt));

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId)).thenReturn(List.of());

    BddLogger.when("He requests the details of that declared activity again");
    service.getDeclaredActivityDetails(declaredActivityId);

    BddLogger.then("The consultation date moves to the latest read");
    verify(declaredActivityRepository).save(activityCaptor.capture());

    assertThat(activityCaptor.getValue().getAssessment())
        .hasValueSatisfying(
            consulted -> assertThat(consulted.consultedAt()).isAfter(lastConsultedAt));
  }

  @Test
  void getDeclaredActivityDetails_should_not_consult_an_assessment_not_sent_yet() {
    BddLogger.given("A logged-in student and his declared activity submitted for assessment");
    Activity activity =
        ActivityFixture.create().withAssessment(new ActivityAssessment(true, 20, null)).toModel();
    DeclaredActivity declaredActivity =
        declaredActivityWith(activity, DeclaredActivityAssessment.requested(Instant.now()));

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId)).thenReturn(List.of());

    BddLogger.when("He requests the details of that declared activity");
    service.getDeclaredActivityDetails(declaredActivityId);

    BddLogger.then("No consultation date is set and nothing is saved");
    assertThat(declaredActivity.getAssessment())
        .hasValueSatisfying(assessment -> assertThat(assessment.consultedAt()).isNull());
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void getDeclaredActivityDetails_should_throw_DeclaredActivityNotFoundException_when_not_found() {
    BddLogger.given("A logged-in student and a non-existent declared activity ID");
    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("He requests declared activity details");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityDetails(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(declaredActivityRepository).findById(eq(declaredActivityId), any(FetchGraph.class));
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void
      getDeclaredActivityDetails_should_throw_UserNotAuthorizedException_when_belonging_to_another_student() {
    BddLogger.given("A logged-in student and a declared activity belonging to another student");
    UUID declaredActivityId = UUID.randomUUID();

    Student anotherStudent = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), anotherStudent, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He requests declared activity details");

    BddLogger.then("A UserNotAuthorizedException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityDetails(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(declaredActivityRepository).findById(eq(declaredActivityId), any(FetchGraph.class));
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void shouldUpdatePeriodSuccessfully() {
    // Given
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getCreatedAt()).thenReturn(Instant.now());

    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(10);

    // When
    BddLogger.when("The service is called with valid dates.");
    declaredActivityService.updateDeclaredActivity(declaredActivityId, startDate, endDate, null);

    // Then
    BddLogger.then("The DeclaredActivity receives the new dates.");
    verify(declaredActivity).setStartDate(startDate);
    verify(declaredActivity).setEndDate(endDate);

    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void updateDeclaredActivity_should_leave_dates_untouched_when_none_provided() {
    BddLogger.given("A logged-in student and his existing declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    BddLogger.when("The service is called without any period field");
    declaredActivityService.updateDeclaredActivity(declaredActivityId, null, null, null);

    BddLogger.then("The DeclaredActivity dates are left untouched but it is still saved");
    verify(declaredActivity, never()).setStartDate(any());
    verify(declaredActivity, never()).setEndDate(any());
    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void updateDeclaredActivity_should_update_valorized_when_provided() {
    BddLogger.given("A logged-in student and his existing declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    BddLogger.when("The service is called with valorized set to true");
    declaredActivityService.updateDeclaredActivity(declaredActivityId, null, null, true);

    BddLogger.then("The DeclaredActivity valorized flag is updated and it is saved");
    verify(declaredActivity).setValorized(true);
    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void updateDeclaredActivity_should_leave_valorized_untouched_when_not_provided() {
    BddLogger.given("A logged-in student and his existing declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    BddLogger.when("The service is called without the valorized field");
    declaredActivityService.updateDeclaredActivity(declaredActivityId, null, null, null);

    BddLogger.then("The DeclaredActivity valorized flag is left untouched but it is still saved");
    verify(declaredActivity, never()).setValorized(anyBoolean());
    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void shouldThrowWhenEndDateBeforeStartDate() {
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getCreatedAt()).thenReturn(Instant.now());

    LocalDate startDate = LocalDate.now().plusDays(10);
    LocalDate endDate = LocalDate.now().plusDays(1);

    FieldValidationException ex =
        Assertions.assertThrows(
            FieldValidationException.class,
            () ->
                declaredActivityService.updateDeclaredActivity(
                    declaredActivityId, startDate, endDate, null));

    Assertions.assertEquals(EErrorCode.END_DATE_BEFORE_START_DATE, ex.getErrorCode());
  }

  @Test
  void shouldThrowWhenStartDateBeforeInscriptionDate() {
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getCreatedAt()).thenReturn(Instant.now());

    LocalDate startDate = LocalDate.now().minusDays(1);
    LocalDate endDate = startDate.plusDays(5);

    BddLogger.when("The service is called with a startDate before the registration date.");

    BusinessException ex =
        Assertions.assertThrows(
            BusinessException.class,
            () ->
                declaredActivityService.updateDeclaredActivity(
                    declaredActivityId, startDate, endDate, null));

    Assertions.assertEquals(
        EErrorCode.DECLARED_ACTIVITY_START_DATE_BEFORE_SUBSCRIPTION, ex.getErrorCode());
  }

  @Test
  void searchDeclaredActivity_should_return_paged_result_from_repository_with_correct_graph() {
    BddLogger.given("A logged-in student, a keyword and page criteria");
    String keyword = "recherche";
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PageInfo pageInfo = new PageInfo(0, 10, 1);

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    PagedResult<DeclaredActivity> expectedResult =
        new PagedResult<>(List.of(declaredActivity), pageInfo);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    ArgumentCaptor<FetchGraph> graphCaptor = ArgumentCaptor.forClass(FetchGraph.class);

    when(declaredActivityRepository.findAllByStudent(
            eq(student), eq(keyword), eq(pageCriteria), graphCaptor.capture()))
        .thenReturn(expectedResult);

    BddLogger.when("He searches for declared activities");
    PagedResult<DeclaredActivity> result = service.searchDeclaredActivity(keyword, pageCriteria);

    BddLogger.then(
        "It should return the paginated result from the repository with the correct FetchGraph");
    assertThat(result).isEqualTo(expectedResult);

    assertThat(graphCaptor.getValue().children()).containsKey("activity");
  }

  @Test
  void updateReflection_should_throw_UserNotAuthorizedException_when_reflection_is_not_enabled() {
    BddLogger.given("A logged-in student and a declared activity where enableReflection is false");

    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().withEnableRefection(false).toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to update the reflection");

    BddLogger.then("A UserNotAuthorizedException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.updateReflection(declaredActivityId, "Ma réflexion"))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void areDeclaredActivitiesUnlocked_should_return_true_when_no_declared_activity_ids() {
    BddLogger.given("an empty declared activity ids list");

    BddLogger.when("checking if declared activities are unlocked");
    boolean result = service.areDeclaredActivitiesUnlocked(List.of());

    BddLogger.then("it should return true without calling repositories");
    assertThat(result).isTrue();
    verify(declaredActivityRepository, never()).findAllById(anyList());
    verify(feedbackRepository, never()).findDeclaredActivityIdsHavingActiveFeedbacks(anyList());
  }

  @Test
  void
      areDeclaredActivitiesUnlocked_should_return_true_when_declared_activities_have_no_feedback_and_are_not_completed() {
    BddLogger.given("declared activities without feedback and not completed");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(declaredActivityRepository.findAllById(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(
            List.of(declaredActivityId)))
        .thenReturn(List.of());

    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.empty());
    when(declaredActivity.getStartedAt()).thenReturn(Optional.of(Instant.now().minusSeconds(60)));

    BddLogger.when("checking if declared activities are unlocked");
    boolean result = service.areDeclaredActivitiesUnlocked(List.of(declaredActivityId));

    BddLogger.then("it should return true");
    assertThat(result).isTrue();
  }

  @Test
  void areDeclaredActivitiesUnlocked_should_return_false_when_declared_activity_has_feedback() {
    BddLogger.given("a declared activity with feedback");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(declaredActivityRepository.findAllById(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(
            List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivityId));

    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.empty());

    BddLogger.when("checking if declared activities are unlocked");
    boolean result = service.areDeclaredActivitiesUnlocked(List.of(declaredActivityId));

    BddLogger.then("it should return false");
    assertThat(result).isFalse();
  }

  @Test
  void areDeclaredActivitiesUnlocked_should_return_false_when_declared_activity_is_completed() {
    BddLogger.given("a completed declared activity");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(declaredActivityRepository.findAllById(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(
            List.of(declaredActivityId)))
        .thenReturn(List.of());

    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.of(Instant.now()));

    BddLogger.when("checking if declared activities are unlocked");
    boolean result = service.areDeclaredActivitiesUnlocked(List.of(declaredActivityId));

    BddLogger.then("it should return false");
    assertThat(result).isFalse();
  }

  @Test
  void checkDeclaredActivitiesUnlocked_should_throw_when_declared_activity_is_submitted() {
    BddLogger.given("a submitted declared activity");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(declaredActivityRepository.findAllById(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(
            List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivityId));

    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.empty());

    BddLogger.when("checking declared activities lock");

    BddLogger.then("it should throw DeclaredActivityLockedException");
    assertThatThrownBy(() -> service.checkDeclaredActivitiesUnlocked(List.of(declaredActivityId)))
        .isInstanceOf(DeclaredActivityLockedException.class);
  }

  @Test
  void checkDeclaredActivitiesUnlocked_should_not_throw_when_declared_activities_are_unlocked() {
    BddLogger.given("unlocked declared activities");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(declaredActivityRepository.findAllById(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(
            List.of(declaredActivityId)))
        .thenReturn(List.of());

    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.empty());
    when(declaredActivity.getStartedAt()).thenReturn(Optional.of(Instant.now().minusSeconds(60)));

    BddLogger.when("checking declared activities lock");

    BddLogger.then("it should not throw");
    assertThatCode(() -> service.checkDeclaredActivitiesUnlocked(List.of(declaredActivityId)))
        .doesNotThrowAnyException();
  }

  @Test
  void countEnrolledStudents_should_delegate_to_repository() {
    BddLogger.given("an activity with enrolled students");
    Activity activity = ActivityFixture.create().toModel();
    when(declaredActivityRepository.countEnrolledByActivity(activity)).thenReturn(3);

    BddLogger.when("counting enrolled students");
    int count = service.countEnrolledStudents(activity);

    BddLogger.then("it should return the repository count");
    assertThat(count).isEqualTo(3);
  }

  @Test
  void countEnrolledStudents_should_return_zero_when_no_students_enrolled() {
    BddLogger.given("an activity with no enrolled students");
    Activity activity = ActivityFixture.create().toModel();
    when(declaredActivityRepository.countEnrolledByActivity(activity)).thenReturn(0);

    BddLogger.when("counting enrolled students");
    int count = service.countEnrolledStudents(activity);

    BddLogger.then("it should return zero");
    assertThat(count).isZero();
  }

  @Test
  void countUnsubscriptionsSince_should_delegate_to_repository() {
    BddLogger.given("an activity left by students since a given date");
    Activity activity = ActivityFixture.create().toModel();
    Instant since = Instant.now().minus(Duration.ofDays(30));
    when(declaredActivityRepository.countUnsubscribedByActivitySince(activity, since))
        .thenReturn(5);

    BddLogger.when("counting the unsubscriptions since this date");
    int count = service.countUnsubscriptionsSince(activity, since);

    BddLogger.then("it should return the repository count");
    assertThat(count).isEqualTo(5);
  }

  @Test
  void getEnrolledStudents_should_return_declared_activities_of_the_activity() {
    BddLogger.given("an activity with two enrolled students");
    Activity activity = ActivityFixture.create().toModel();
    Student student1 = StudentFixture.create().toModel();
    Student student2 = StudentFixture.create().toModel();
    DeclaredActivity declaredActivity1 =
        DeclaredActivity.create(
            UUID.randomUUID(), student1, activity, null, null, null, null, null);
    DeclaredActivity declaredActivity2 =
        DeclaredActivity.create(
            UUID.randomUUID(), student2, activity, null, null, null, null, null);
    when(declaredActivityRepository.findAllEnrolledByActivity(eq(activity), any(FetchGraph.class)))
        .thenReturn(List.of(declaredActivity1, declaredActivity2));

    BddLogger.when("getting the enrolled students");
    List<DeclaredActivity> result = service.getEnrolledStudents(activity);

    BddLogger.then("it should return the declared activities of the activity");
    assertThat(result).containsExactly(declaredActivity1, declaredActivity2);
  }

  @Test
  void getEnrolledStudentsNotViewedSince_should_delegate_the_filtering_to_the_repository() {
    BddLogger.given("an activity with one student having not consulted it for a month");
    Activity activity = ActivityFixture.create().toModel();
    Student student = StudentFixture.create().toModel();
    Instant since = Instant.now().minus(Duration.ofDays(30));
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);
    when(declaredActivityRepository.findAllEnrolledNotViewedSince(
            eq(activity), eq(since), any(FetchGraph.class)))
        .thenReturn(List.of(declaredActivity));

    BddLogger.when("getting the enrolled students without recent consultation");
    List<DeclaredActivity> result = service.getEnrolledStudentsNotViewedSince(activity, since);

    BddLogger.then("it should return the declared activities filtered by the repository");
    assertThat(result).containsExactly(declaredActivity);
  }

  @Test
  void getEnrolledStudentIds_should_delegate_to_repository() {
    BddLogger.given("an activity with two enrolled students");
    Activity activity = ActivityFixture.create().toModel();
    List<UUID> studentIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    when(declaredActivityRepository.findEnrolledStudentIdsByActivity(activity))
        .thenReturn(studentIds);

    BddLogger.when("getting the ids of the enrolled students");
    List<UUID> result = service.getEnrolledStudentIds(activity);

    BddLogger.then("it should return the ids of the repository");
    assertThat(result).isEqualTo(studentIds);
  }

  @Test
  void finish_should_throw_DeclaredActivityUnsubscribedException_when_unsubscribed() {
    BddLogger.given("A logged-in student and an activity he unsubscribed from");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = unsubscribedDeclaredActivity();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to finish it");

    BddLogger.then("A DeclaredActivityUnsubscribedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityUnsubscribedException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void updateReflection_should_throw_DeclaredActivityUnsubscribedException_when_unsubscribed() {
    BddLogger.given("A logged-in student and an activity he unsubscribed from");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = unsubscribedDeclaredActivity();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to update its reflection");

    BddLogger.then("A DeclaredActivityUnsubscribedException is thrown");
    assertThatThrownBy(() -> service.updateReflection(declaredActivityId, "new reflection"))
        .isInstanceOf(DeclaredActivityUnsubscribedException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void
      updateDeclaredActivity_should_throw_DeclaredActivityUnsubscribedException_when_unsubscribed() {
    BddLogger.given("A logged-in student and an activity he unsubscribed from");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = unsubscribedDeclaredActivity();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to update it");

    BddLogger.then("A DeclaredActivityUnsubscribedException is thrown");
    assertThatThrownBy(
            () -> service.updateDeclaredActivity(declaredActivityId, null, null, Boolean.TRUE))
        .isInstanceOf(DeclaredActivityUnsubscribedException.class);
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void getDeclaredActivityStatus_should_return_UNSUBSCRIBED_when_declared_activity_unsubscribed() {
    BddLogger.given("A finished declared activity the student then unsubscribed from");
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            ActivityFixture.create().toModel(),
            Instant.now(),
            null,
            null,
            null,
            Instant.now());
    declaredActivity.unsubscribe(Instant.now());

    when(feedbackRepository.findDeclaredActivityIdsHavingActiveFeedbacks(
            List.of(declaredActivity.getId())))
        .thenReturn(List.of());

    BddLogger.when("Its status is resolved");
    var status = service.getDeclaredActivityStatus(declaredActivity);

    BddLogger.then("UNSUBSCRIBED takes precedence over every other status");
    assertThat(status).isEqualTo(EDeclaredActivityStatus.UNSUBSCRIBED);
  }

  @Test
  void isEnrolled_should_return_false_when_the_student_unsubscribed() {
    BddLogger.given("An activity the student unsubscribed from");
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);
    declaredActivity.unsubscribe(Instant.now());

    when(declaredActivityRepository.findByActivity(student, activity))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("Checking whether he is enrolled");

    BddLogger.then("He is not considered enrolled anymore");
    assertThat(service.isEnrolled(activity, student)).isFalse();
  }

  @Test
  void isEnrolled_should_return_true_when_the_student_is_still_subscribed() {
    BddLogger.given("An activity the student is subscribed to");
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(declaredActivityRepository.findByActivity(student, activity))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("Checking whether he is enrolled");

    BddLogger.then("He is considered enrolled");
    assertThat(service.isEnrolled(activity, student)).isTrue();
  }

  private DeclaredActivity assessableDeclaredActivity() {
    return assessableDeclaredActivity(new ActivityAssessment(true, null, null));
  }

  private DeclaredActivity assessableDeclaredActivity(ActivityAssessment assessment) {
    return DeclaredActivity.create(
        UUID.randomUUID(),
        student,
        ActivityFixture.create().withAssessment(assessment).toModel(),
        Instant.now(),
        null,
        null,
        null,
        null);
  }

  private DeclaredActivity unsubscribedDeclaredActivity() {
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            ActivityFixture.create().toModel(),
            Instant.now(),
            null,
            null,
            null,
            null);
    declaredActivity.unsubscribe(Instant.now());
    return declaredActivity;
  }

  @Test
  void getEnrolledStudents_should_return_empty_list_when_no_students_enrolled() {
    BddLogger.given("an activity with no enrolled students");
    Activity activity = ActivityFixture.create().toModel();
    when(declaredActivityRepository.findAllEnrolledByActivity(eq(activity), any(FetchGraph.class)))
        .thenReturn(List.of());

    BddLogger.when("getting the enrolled students");
    List<DeclaredActivity> result = service.getEnrolledStudents(activity);

    BddLogger.then("it should return an empty list");
    assertThat(result).isEmpty();
  }

  private DeclaredActivity stubDeclaredActivity(Instant unsubscribe) {
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            declaredActivityId,
            student,
            activity,
            Instant.now(),
            "my reflection",
            null,
            null,
            null);

    declaredActivity.unsubscribe(unsubscribe);

    UUID declaredActivityId = declaredActivity.getId();
    when(declaredActivityRepository.findById(eq(declaredActivityId), any()))
        .thenReturn(Optional.of(declaredActivity));
    return declaredActivity;
  }

  @Test
  void delete_should_throw_when_declaredActivity_not_found() {
    when(declaredActivityRepository.findById(eq(declaredActivityId), any()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> declaredActivityService.delete(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verifyNoInteractions(associationService, feedbackService);
    verify(declaredActivityRepository, never()).removeFromDatabase(any());
  }

  @Test
  void delete_should_throw_when_declaredActivity_not_unsubscribed() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    stubDeclaredActivity(null);
    assertThatThrownBy(() -> declaredActivityService.delete(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotUnsubscribedException.class);
    verifyNoInteractions(associationService, feedbackService);
    verify(declaredActivityRepository, never()).removeFromDatabase(any());
  }

  @Test
  void
      delete_should_delete_associations_notifications_feedbacks_then_declared_activity_when_unsubscribed() {
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    DeclaredActivity declaredActivity = stubDeclaredActivity(Instant.now());
    UUID feedbackId = UUID.randomUUID();
    Feedback feedback = mock(Feedback.class);
    when(feedback.getId()).thenReturn(feedbackId);
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
        .thenReturn(List.of(feedback));
    declaredActivityService.delete(declaredActivityId);
    InOrder inOrder =
        inOrder(
            associationService, notificationService, feedbackService, declaredActivityRepository);
    inOrder
        .verify(associationService)
        .deleteAllOf(List.of(declaredActivityId), DeclaredActivity.class);
    inOrder
        .verify(notificationService)
        .deleteNotificationsOf(ENotificationType.ASK_FOR_FEEDBACK, List.of(feedbackId));
    inOrder.verify(feedbackService).deleteByDeclaredActivityId(declaredActivity);
    inOrder.verify(declaredActivityRepository).removeFromDatabase(declaredActivity);
  }

  @Test
  void delete_should_reject_when_activity_is_not_owned_by_connected_student() {
    Student connected = StudentFixture.create().toModel();
    when(loggedInUserService.getLoggedInStudent()).thenReturn(connected);
    stubDeclaredActivity(Instant.now());
    assertThatThrownBy(() -> declaredActivityService.delete(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);
    verifyNoInteractions(associationService, feedbackService);
    verify(declaredActivityRepository, never()).removeFromDatabase(any());
  }
}
