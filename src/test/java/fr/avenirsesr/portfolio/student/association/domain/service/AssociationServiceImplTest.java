package fr.avenirsesr.portfolio.student.association.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
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
class AssociationServiceImplTest {

  @Mock private AssociationRepository associationRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private TraceService traceService;
  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private DeclaredSkillProgressService declaredSkillProgressService;
  @Mock private DeclaredExperienceService declaredExperienceService;

  @InjectMocks private AssociationServiceImpl service;

  private static Association association(UUID id1, UUID id2, EAssociationType associationType) {
    return Association.create(id1, id2, associationType);
  }

  private DeclaredSkillProgress declaredSkillProgressOf(UUID id, Student student) {
    var declaredSkillProgress = mock(DeclaredSkillProgress.class);
    when(declaredSkillProgress.getId()).thenReturn(id);
    lenient().when(declaredSkillProgress.getStudent()).thenReturn(student);

    return declaredSkillProgress;
  }

  private void givenAssociationsOf(UUID id, Class<?> clazz, List<Association> associations) {
    when(associationRepository.findAllOf(id, clazz, EAssociationType.getAllBy(clazz)))
        .thenReturn(associations);
  }

  @Test
  void createAll_should_save_associations_when_valid() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data =
        List.of(new AssociationData(activityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE));

    when(associationRepository.findAllIn(data)).thenReturn(List.of());

    when(associationRepository.saveAll(any())).thenAnswer(i -> i.getArguments()[0]);

    var result = service.createAll(data);

    assertThat(result).hasSize(1);

    verify(associationRepository).saveAll(any());
  }

  @Test
  void createAll_should_throw_AssociationAlreadyExistException() {

    UUID traceId = UUID.randomUUID();
    UUID activityId = UUID.randomUUID();

    var data =
        List.of(new AssociationData(activityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE));

    when(associationRepository.findAllIn(data)).thenReturn(List.of(mock(Association.class)));

    assertThatThrownBy(() -> service.createAll(data))
        .isInstanceOf(AssociationAlreadyExistException.class);
  }

  @Test
  void deleteAllByIds_should_delete_when_all_ids_exist() {

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    Association a1 = mock(Association.class);
    Association a2 = mock(Association.class);

    when(a1.getId()).thenReturn(id1);
    when(a2.getId()).thenReturn(id2);

    when(associationRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(a1, a2));

    service.deleteAllByIds(List.of(id1, id2));

    verify(associationRepository).removeAllFromDatabase(List.of(a1, a2));
  }

  @Test
  void deleteAllByIds_should_throw_when_one_id_not_found() {

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    Association a1 = mock(Association.class);

    when(a1.getId()).thenReturn(id1);

    when(associationRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(a1));

    assertThatThrownBy(() -> service.deleteAllByIds(List.of(id1, id2)))
        .isInstanceOf(AssociationDoesNotExistException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void deleteAllByIds_should_throw_when_none_found() {

    UUID id1 = UUID.randomUUID();

    when(associationRepository.findAllById(List.of(id1))).thenReturn(List.of());

    assertThatThrownBy(() -> service.deleteAllByIds(List.of(id1)))
        .isInstanceOf(AssociationDoesNotExistException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void countAllOf_should_delegate_to_repository() {
    UUID traceId = UUID.randomUUID();
    Map<UUID, Long> expected = Map.of(traceId, 2L);

    when(associationRepository.countAllOf(
            List.of(traceId), Trace.class, EAssociationType.DECLARED_ACTIVITY_TRACE))
        .thenReturn(expected);

    var result =
        service.countAllOf(List.of(traceId), Trace.class, EAssociationType.DECLARED_ACTIVITY_TRACE);

    assertThat(result).isSameAs(expected);
  }

  @Test
  void countAllOf_should_return_empty_map_without_querying_when_ids_empty() {
    var result =
        service.countAllOf(List.of(), Trace.class, EAssociationType.DECLARED_ACTIVITY_TRACE);

    assertThat(result).isEmpty();
    verify(associationRepository, never()).countAllOf(any(), any(), any());
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

  @Test
  void associate_should_create_the_associations_of_the_element() {
    var student = StudentFixture.create().toModel();
    UUID traceId = UUID.randomUUID();
    UUID skillId = UUID.randomUUID();

    var declaredSkillProgress = declaredSkillProgressOf(skillId, student);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of(declaredSkillProgress));
    when(associationRepository.findAllIn(anyList())).thenReturn(List.of());
    when(associationRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

    service.associate(
        traceId, Trace.class, List.of(skillId), EAssociationType.TRACE_DECLARED_SKILL);

    verify(associationRepository)
        .findAllIn(
            List.of(new AssociationData(traceId, skillId, EAssociationType.TRACE_DECLARED_SKILL)));
    verify(associationRepository).saveAll(anyList());
  }

  @Test
  void associate_should_create_the_associations_when_the_element_is_the_second_key() {
    var student = StudentFixture.create().toModel();
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getStudent()).thenReturn(student);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(associationRepository.findAllIn(anyList())).thenReturn(List.of());
    when(associationRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

    service.associate(
        traceId,
        Trace.class,
        List.of(declaredActivityId),
        EAssociationType.DECLARED_ACTIVITY_TRACE);

    verify(associationRepository)
        .findAllIn(
            List.of(
                new AssociationData(
                    declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE)));
  }

  @Test
  void associate_should_create_a_single_association_for_a_duplicated_id() {
    var student = StudentFixture.create().toModel();
    UUID traceId = UUID.randomUUID();
    UUID skillId = UUID.randomUUID();

    var declaredSkillProgress = declaredSkillProgressOf(skillId, student);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of(declaredSkillProgress));
    when(associationRepository.findAllIn(anyList())).thenReturn(List.of());
    when(associationRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);

    service.associate(
        traceId, Trace.class, List.of(skillId, skillId), EAssociationType.TRACE_DECLARED_SKILL);

    verify(associationRepository)
        .findAllIn(
            List.of(new AssociationData(traceId, skillId, EAssociationType.TRACE_DECLARED_SKILL)));
  }

  @Test
  void associate_should_throw_when_an_associated_element_does_not_exist() {
    UUID traceId = UUID.randomUUID();
    UUID skillId = UUID.randomUUID();

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of());

    assertThatThrownBy(
            () ->
                service.associate(
                    traceId, Trace.class, List.of(skillId), EAssociationType.TRACE_DECLARED_SKILL))
        .isInstanceOf(DeclaredSkillProgressNotFoundException.class);

    verify(associationRepository, never()).saveAll(anyList());
  }

  @Test
  void associate_should_throw_when_an_associated_element_belongs_to_another_student() {
    UUID traceId = UUID.randomUUID();
    UUID skillId = UUID.randomUUID();

    var declaredSkillProgress = declaredSkillProgressOf(skillId, StudentFixture.create().toModel());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(StudentFixture.create().toModel());
    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of(declaredSkillProgress));

    assertThatThrownBy(
            () ->
                service.associate(
                    traceId, Trace.class, List.of(skillId), EAssociationType.TRACE_DECLARED_SKILL))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(associationRepository, never()).saveAll(anyList());
  }

  @Test
  void unassociate_should_delete_only_the_given_associations() {
    UUID traceId = UUID.randomUUID();

    var skillAssociation =
        association(traceId, UUID.randomUUID(), EAssociationType.TRACE_DECLARED_SKILL);
    var experienceAssociation =
        association(traceId, UUID.randomUUID(), EAssociationType.TRACE_DECLARED_EXPERIENCE);

    givenAssociationsOf(traceId, Trace.class, List.of(skillAssociation, experienceAssociation));
    when(associationRepository.findAllById(List.of(skillAssociation.getId())))
        .thenReturn(List.of(skillAssociation));

    service.unassociate(traceId, Trace.class, List.of(skillAssociation.getId()));

    verify(associationRepository).removeAllFromDatabase(List.of(skillAssociation));
    verifyNoInteractions(declaredActivityService);
  }

  @Test
  void unassociate_should_throw_when_an_association_is_not_one_of_the_element() {
    UUID traceId = UUID.randomUUID();

    var traceAssociation =
        association(traceId, UUID.randomUUID(), EAssociationType.TRACE_DECLARED_SKILL);
    UUID otherAssociationId = UUID.randomUUID();

    givenAssociationsOf(traceId, Trace.class, List.of(traceAssociation));

    assertThatThrownBy(
            () ->
                service.unassociate(
                    traceId, Trace.class, List.of(traceAssociation.getId(), otherAssociationId)))
        .isInstanceOf(AssociationDoesNotExistException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void unassociate_should_throw_when_an_associated_declared_activity_is_finished() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var activityAssociation =
        association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE);

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.of(Instant.now()));

    givenAssociationsOf(traceId, Trace.class, List.of(activityAssociation));
    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));

    assertThatThrownBy(
            () -> service.unassociate(traceId, Trace.class, List.of(activityAssociation.getId())))
        .isInstanceOf(DeclaredActivityAlreadyFinishedException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void unassociate_should_check_the_declared_activity_of_a_declared_skill_association() {
    UUID declaredSkillProgressId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var activityAssociation =
        association(
            declaredActivityId,
            declaredSkillProgressId,
            EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getFinishedAt()).thenReturn(Optional.empty());

    givenAssociationsOf(
        declaredSkillProgressId, DeclaredSkillProgress.class, List.of(activityAssociation));
    when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(declaredActivityId)))
        .thenReturn(List.of(declaredActivity));
    when(associationRepository.findAllById(List.of(activityAssociation.getId())))
        .thenReturn(List.of(activityAssociation));

    service.unassociate(
        declaredSkillProgressId, DeclaredSkillProgress.class, List.of(activityAssociation.getId()));

    verify(associationRepository).removeAllFromDatabase(List.of(activityAssociation));
  }
}
