package fr.avenirsesr.portfolio.student.activity.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityUnsubscribedException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.exception.MaximumAssociationReachedException;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityAssociationStrategyTest {

  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private AssociationService associationService;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private DeclaredActivityAssociationStrategy strategy;

  private DeclaredActivity declaredActivityOf(UUID id, Student student) {
    var declaredActivity = mock(DeclaredActivity.class);
    lenient().when(declaredActivity.getId()).thenReturn(id);
    lenient().when(declaredActivity.getStudent()).thenReturn(student);

    return declaredActivity;
  }

  @Test
  void getContextType_should_return_the_declared_activity_context() {
    assertThat(strategy.getContextType()).isEqualTo(EAssociationContextType.DECLARED_ACTIVITY);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_a_declared_activity_does_not_exist() {
    UUID declaredActivityId = UUID.randomUUID();

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> strategy.checkLoggedInStudentOwns(List.of(declaredActivityId)))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_it_belongs_to_another_student() {
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivity =
        declaredActivityOf(declaredActivityId, StudentFixture.create().toModel());

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(StudentFixture.create().toModel());

    assertThatThrownBy(() -> strategy.checkLoggedInStudentOwns(List.of(declaredActivityId)))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void checkLoggedInStudentCanAssociate_should_throw_when_the_student_unsubscribed() {
    var student = StudentFixture.create().toModel();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivity = declaredActivityOf(declaredActivityId, student);
    when(declaredActivity.isUnsubscribed()).thenReturn(true);

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    assertThatThrownBy(
            () ->
                strategy.checkLoggedInStudentCanAssociate(
                    declaredActivityId, EAssociationType.DECLARED_ACTIVITY_TRACE, 1))
        .isInstanceOf(DeclaredActivityUnsubscribedException.class);
  }

  @Test
  void checkLoggedInStudentCanAssociate_should_throw_when_the_allowed_traces_are_reached() {
    var student = StudentFixture.create().toModel();
    UUID declaredActivityId = UUID.randomUUID();

    var activity = mock(Activity.class);
    when(activity.getTraceAllowedAssociations()).thenReturn(2);

    var declaredActivity = declaredActivityOf(declaredActivityId, student);
    when(declaredActivity.getActivity()).thenReturn(activity);

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(associationService.getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(EAssociationType.DECLARED_ACTIVITY_TRACE)))
        .thenReturn(List.of(mock(Association.class)));

    assertThatThrownBy(
            () ->
                strategy.checkLoggedInStudentCanAssociate(
                    declaredActivityId, EAssociationType.DECLARED_ACTIVITY_TRACE, 2))
        .isInstanceOf(MaximumAssociationReachedException.class);
  }

  @Test
  void checkLoggedInStudentCanAssociate_should_not_count_the_traces_when_they_are_unlimited() {
    var student = StudentFixture.create().toModel();
    UUID declaredActivityId = UUID.randomUUID();

    var activity = mock(Activity.class);
    when(activity.getTraceAllowedAssociations()).thenReturn(-1);

    var declaredActivity = declaredActivityOf(declaredActivityId, student);
    when(declaredActivity.getActivity()).thenReturn(activity);

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    strategy.checkLoggedInStudentCanAssociate(
        declaredActivityId, EAssociationType.DECLARED_ACTIVITY_TRACE, 10);

    verify(associationService, never()).getAllOf(any(UUID.class), any(), any());
  }

  @Test
  void checkLoggedInStudentCanAssociate_should_not_count_the_traces_for_another_association() {
    var student = StudentFixture.create().toModel();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivity = declaredActivityOf(declaredActivityId, student);

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    strategy.checkLoggedInStudentCanAssociate(
        declaredActivityId, EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL, 1);

    verify(associationService, never()).getAllOf(any(UUID.class), any(), any());
  }

  @Test
  void checkLoggedInStudentCanUnassociate_should_throw_when_the_activity_is_finished() {
    var student = StudentFixture.create().toModel();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivity = declaredActivityOf(declaredActivityId, student);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.of(Instant.now()));

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    assertThatThrownBy(
            () -> strategy.checkLoggedInStudentCanUnassociate(List.of(declaredActivityId)))
        .isInstanceOf(DeclaredActivityAlreadyFinishedException.class);
  }

  @Test
  void search_should_disable_the_finished_declared_activities() {
    UUID declaredActivityId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    var activity = mock(Activity.class);
    when(activity.getTitle()).thenReturn("Activity");
    when(activity.getThematic()).thenReturn(EActivityThematic.EXPERIENCES);

    var declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getActivity()).thenReturn(activity);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.of(Instant.now()));

    when(declaredActivityService.searchDeclaredActivity("kw", pageCriteria))
        .thenReturn(new PagedResult<>(List.of(declaredActivity), new PageInfo(0, 10, 1)));

    var result = strategy.search("kw", AssociationSearchFilter.NONE, pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                declaredActivityId, "Activity", EActivityThematic.EXPERIENCES.name(), true));
  }

  @Test
  void toAssociatedElements_should_return_the_declared_activities_with_their_status() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);

    var association =
        Association.create(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE);

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(declaredActivityService.getDeclaredActivityStatus(List.of(declaredActivity)))
        .thenReturn(Map.of(declaredActivity, EDeclaredActivityStatus.IN_PROGRESS));

    var result = strategy.toAssociatedElements(List.of(association), Trace.class, false);

    assertThat(result.declaredActivityAssociations())
        .singleElement()
        .satisfies(
            data -> {
              assertThat(data.associationId()).isEqualTo(association.getId());
              assertThat(data.declaredActivity()).isEqualTo(declaredActivity);
              assertThat(data.status()).isEqualTo(EDeclaredActivityStatus.IN_PROGRESS);
            });
  }

  @Test
  void toAssociatedElements_should_fetch_only_the_not_completed_activities_when_asked() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);

    var association =
        Association.create(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE);

    when(declaredActivityService.findAllNotCompletedActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(declaredActivityService.getDeclaredActivityStatus(List.of(declaredActivity)))
        .thenReturn(Map.of(declaredActivity, EDeclaredActivityStatus.IN_PROGRESS));

    strategy.toAssociatedElements(List.of(association), Trace.class, true);

    verify(declaredActivityService, never()).findAllDeclaredActivitiesByIds(any());
  }

  @Test
  void toAssociatedElements_should_throw_when_a_declared_activity_is_missing() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var association =
        Association.create(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE);

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of());
    when(declaredActivityService.getDeclaredActivityStatus(List.of())).thenReturn(Map.of());

    assertThatThrownBy(
            () -> strategy.toAssociatedElements(List.of(association), Trace.class, false))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
  }
}
