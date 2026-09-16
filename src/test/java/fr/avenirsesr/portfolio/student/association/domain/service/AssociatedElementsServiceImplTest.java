package fr.avenirsesr.portfolio.student.association.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssociatedElementsServiceImplTest {

  @Mock private AssociationService associationService;
  @Mock private TraceService traceService;
  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private DeclaredSkillProgressService declaredSkillProgressService;
  @Mock private DeclaredExperienceService declaredExperienceService;

  @InjectMocks private AssociatedElementsServiceImpl service;

  private static Association association(UUID id1, UUID id2, EAssociationType associationType) {
    return Association.create(id1, id2, associationType);
  }

  private void givenAssociationsOf(UUID id, Class<?> clazz, List<Association> associations) {
    when(associationService.getAllOf(id, clazz, EAssociationType.getAllBy(clazz)))
        .thenReturn(associations);
  }

  @Test
  void getAllAssociatedElementsOf_should_return_every_element_associated_to_a_trace() {
    UUID traceId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    DeclaredSkillProgress declaredSkillProgress = mock(DeclaredSkillProgress.class);
    DeclaredExperience declaredExperience = mock(DeclaredExperience.class);

    UUID declaredActivityId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();
    UUID declaredExperienceId = UUID.randomUUID();

    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredSkillProgress.getId()).thenReturn(declaredSkillProgressId);
    when(declaredExperience.getId()).thenReturn(declaredExperienceId);

    givenAssociationsOf(
        traceId,
        Trace.class,
        List.of(
            association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE),
            association(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL),
            association(
                traceId, declaredExperienceId, EAssociationType.TRACE_DECLARED_EXPERIENCE)));

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(declaredActivityService.getDeclaredActivityStatus(List.of(declaredActivity)))
        .thenReturn(Map.of(declaredActivity, EDeclaredActivityStatus.IN_PROGRESS));
    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            List.of(declaredSkillProgressId)))
        .thenReturn(List.of(declaredSkillProgress));
    when(declaredExperienceService.findAllByIds(List.of(declaredExperienceId)))
        .thenReturn(List.of(declaredExperience));

    var result = service.getAllAssociatedElementsOf(traceId, Trace.class);

    assertThat(result.traceAssociations()).isEmpty();
    assertThat(result.declaredActivityAssociations())
        .singleElement()
        .satisfies(
            data -> {
              assertThat(data.declaredActivity()).isEqualTo(declaredActivity);
              assertThat(data.status()).isEqualTo(EDeclaredActivityStatus.IN_PROGRESS);
            });
    assertThat(result.declaredSkillAssociations())
        .singleElement()
        .satisfies(data -> assertThat(data.declaredSkill()).isEqualTo(declaredSkillProgress));
    assertThat(result.declaredExperienceAssociations())
        .singleElement()
        .satisfies(data -> assertThat(data.declaredExperience()).isEqualTo(declaredExperience));
  }

  @Test
  void getAllAssociatedElementsOf_should_return_every_element_associated_to_a_declared_skill() {
    UUID declaredSkillProgressId = UUID.randomUUID();

    Trace trace = mock(Trace.class);
    UUID traceId = UUID.randomUUID();
    when(trace.getId()).thenReturn(traceId);

    givenAssociationsOf(
        declaredSkillProgressId,
        DeclaredSkillProgress.class,
        List.of(
            association(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL)));

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));

    var result =
        service.getAllAssociatedElementsOf(declaredSkillProgressId, DeclaredSkillProgress.class);

    assertThat(result.traceAssociations())
        .singleElement()
        .satisfies(data -> assertThat(data.trace()).isEqualTo(trace));
    assertThat(result.declaredActivityAssociations()).isEmpty();
    assertThat(result.declaredSkillAssociations()).isEmpty();
    assertThat(result.declaredExperienceAssociations()).isEmpty();
  }

  @Test
  void getAllAssociatedElementsOf_should_keep_the_association_order() {
    UUID declaredSkillProgressId = UUID.randomUUID();

    DeclaredExperience firstExperience = mock(DeclaredExperience.class);
    DeclaredExperience secondExperience = mock(DeclaredExperience.class);

    UUID firstExperienceId = UUID.randomUUID();
    UUID secondExperienceId = UUID.randomUUID();

    when(firstExperience.getId()).thenReturn(firstExperienceId);
    when(secondExperience.getId()).thenReturn(secondExperienceId);

    givenAssociationsOf(
        declaredSkillProgressId,
        DeclaredSkillProgress.class,
        List.of(
            association(
                firstExperienceId,
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL),
            association(
                secondExperienceId,
                declaredSkillProgressId,
                EAssociationType.DECLARED_EXPERIENCE_DECLARED_SKILL)));

    when(declaredExperienceService.findAllByIds(List.of(firstExperienceId, secondExperienceId)))
        .thenReturn(List.of(secondExperience, firstExperience));

    var result =
        service.getAllAssociatedElementsOf(declaredSkillProgressId, DeclaredSkillProgress.class);

    assertThat(result.declaredExperienceAssociations())
        .extracting(data -> data.declaredExperience())
        .containsExactly(firstExperience, secondExperience);
  }

  @Test
  void getAllAssociatedElementsOf_should_fetch_only_not_completed_activities_when_asked() {
    UUID traceId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    UUID declaredActivityId = UUID.randomUUID();
    when(declaredActivity.getId()).thenReturn(declaredActivityId);

    givenAssociationsOf(
        traceId,
        Trace.class,
        List.of(
            association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE)));

    when(declaredActivityService.findAllNotCompletedActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(declaredActivityService.getDeclaredActivityStatus(List.of(declaredActivity)))
        .thenReturn(Map.of(declaredActivity, EDeclaredActivityStatus.IN_PROGRESS));

    var result = service.getAllAssociatedElementsOf(traceId, Trace.class, true);

    assertThat(result.declaredActivityAssociations()).hasSize(1);
    verify(declaredActivityService).findAllNotCompletedActivitiesByIds(List.of(declaredActivityId));
    verify(declaredActivityService, never()).findAllDeclaredActivitiesByIds(any());
  }

  @Test
  void getAllAssociatedElementsOf_should_not_fetch_anything_when_there_is_no_association() {
    UUID traceId = UUID.randomUUID();

    givenAssociationsOf(traceId, Trace.class, List.of());

    var result = service.getAllAssociatedElementsOf(traceId, Trace.class);

    assertThat(result.traceAssociations()).isEmpty();
    assertThat(result.declaredActivityAssociations()).isEmpty();
    assertThat(result.declaredSkillAssociations()).isEmpty();
    assertThat(result.declaredExperienceAssociations()).isEmpty();
    verifyNoInteractions(
        traceService,
        declaredActivityService,
        declaredSkillProgressService,
        declaredExperienceService);
  }

  @Test
  void getAllAssociatedElementsOf_should_throw_when_a_trace_is_missing() {
    UUID declaredActivityId = UUID.randomUUID();
    UUID traceId = UUID.randomUUID();

    givenAssociationsOf(
        declaredActivityId,
        DeclaredActivity.class,
        List.of(
            association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE)));

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of());

    assertThatThrownBy(
            () -> service.getAllAssociatedElementsOf(declaredActivityId, DeclaredActivity.class))
        .isInstanceOf(TraceNotFoundException.class);
  }

  @Test
  void getAllAssociatedElementsOf_should_throw_when_a_declared_activity_is_missing() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    givenAssociationsOf(
        traceId,
        Trace.class,
        List.of(
            association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE)));

    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> service.getAllAssociatedElementsOf(traceId, Trace.class))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
  }

  @Test
  void getAllAssociatedElementsOf_should_throw_when_a_declared_skill_is_missing() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    givenAssociationsOf(
        traceId,
        Trace.class,
        List.of(
            association(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL)));

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            List.of(declaredSkillProgressId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> service.getAllAssociatedElementsOf(traceId, Trace.class))
        .isInstanceOf(DeclaredSkillProgressNotFoundException.class);
  }

  @Test
  void getAllAssociatedElementsOf_should_throw_when_a_declared_experience_is_missing() {
    UUID traceId = UUID.randomUUID();
    UUID declaredExperienceId = UUID.randomUUID();

    givenAssociationsOf(
        traceId,
        Trace.class,
        List.of(
            association(
                traceId, declaredExperienceId, EAssociationType.TRACE_DECLARED_EXPERIENCE)));

    when(declaredExperienceService.findAllByIds(List.of(declaredExperienceId)))
        .thenReturn(List.of());

    assertThatThrownBy(() -> service.getAllAssociatedElementsOf(traceId, Trace.class))
        .isInstanceOf(DeclaredExperienceNotFoundException.class);
  }
}
