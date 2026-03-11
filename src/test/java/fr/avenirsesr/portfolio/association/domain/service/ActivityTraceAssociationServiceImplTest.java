package fr.avenirsesr.portfolio.association.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.ActivityTraceAssociationRepository;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityTraceAssociationServiceImplTest {

  @Mock private LoggedInUserService loggedInUserService;
  @Mock private ActivityTraceAssociationRepository associationRepository;
  @Mock private TraceRepository traceRepository;
  @Mock private DeclaredActivityRepository declaredActivityRepository;

  @InjectMocks private ActivityTraceAssociationServiceImpl service;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
  }

  @Test
  void createAll_should_save_associations_when_valid() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data = List.of(new ActivityTraceAssociationData(activityId, traceId));

    DeclaredActivity activity = mock(DeclaredActivity.class);
    Trace trace = mock(Trace.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(activity.getId()).thenReturn(activityId);
    when(activity.getStudent()).thenReturn(student);

    when(trace.getId()).thenReturn(traceId);
    when(trace.getUser()).thenReturn(student.getUser());

    when(traceRepository.findAllById(anyList())).thenReturn(List.of(trace));
    when(declaredActivityRepository.findAllById(anyList())).thenReturn(List.of(activity));

    when(associationRepository.findAllIn(data)).thenReturn(List.of());

    when(associationRepository.saveAll(any())).thenAnswer(i -> i.getArguments()[0]);

    var result = service.createAll(data);

    assertThat(result).hasSize(1);

    verify(associationRepository).saveAll(any());
  }

  @Test
  void createAll_should_throw_DeclaredActivityNotFoundException() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data = List.of(new ActivityTraceAssociationData(activityId, traceId));

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(traceRepository.findAllById(anyList())).thenReturn(List.of());
    when(declaredActivityRepository.findAllById(anyList())).thenReturn(List.of());

    assertThatThrownBy(() -> service.createAll(data))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
  }

  @Test
  void createAll_should_throw_TraceNotFoundException() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data = List.of(new ActivityTraceAssociationData(activityId, traceId));

    DeclaredActivity activity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(activity.getId()).thenReturn(activityId);

    when(declaredActivityRepository.findAllById(anyList())).thenReturn(List.of(activity));

    when(traceRepository.findAllById(anyList())).thenReturn(List.of());

    assertThatThrownBy(() -> service.createAll(data)).isInstanceOf(TraceNotFoundException.class);
  }

  @Test
  void createAll_should_throw_UserNotAuthorizedException() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data = List.of(new ActivityTraceAssociationData(activityId, traceId));

    Student another = StudentFixture.create().toModel();

    DeclaredActivity activity = mock(DeclaredActivity.class);
    Trace trace = mock(Trace.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(activity.getId()).thenReturn(activityId);
    when(activity.getStudent()).thenReturn(another);

    when(trace.getId()).thenReturn(traceId);

    when(traceRepository.findAllById(anyList())).thenReturn(List.of(trace));

    when(declaredActivityRepository.findAllById(anyList())).thenReturn(List.of(activity));

    assertThatThrownBy(() -> service.createAll(data))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void createAll_should_throw_AssociationAlreadyExistException() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data = List.of(new ActivityTraceAssociationData(activityId, traceId));

    DeclaredActivity activity = mock(DeclaredActivity.class);
    Trace trace = mock(Trace.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(activity.getId()).thenReturn(activityId);
    when(activity.getStudent()).thenReturn(student);

    when(trace.getId()).thenReturn(traceId);
    when(trace.getUser()).thenReturn(student.getUser());

    when(traceRepository.findAllById(anyList())).thenReturn(List.of(trace));

    when(declaredActivityRepository.findAllById(anyList())).thenReturn(List.of(activity));

    when(associationRepository.findAllIn(data))
        .thenReturn(List.of(mock(ActivityTraceAssociation.class)));

    assertThatThrownBy(() -> service.createAll(data))
        .isInstanceOf(AssociationAlreadyExistException.class);
  }
}
