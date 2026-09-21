package fr.avenirsesr.portfolio.student.association.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.WrongClassTypeArgumentException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.student.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.filter.TraceAssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.handler.AssociationContextHandler;
import fr.avenirsesr.portfolio.student.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssociationServiceImplTest {

  @Mock private AssociationRepository associationRepository;
  @Mock private AssociationContextHandler traceHandler;
  @Mock private AssociationContextHandler declaredActivityHandler;
  @Mock private AssociationContextHandler declaredSkillHandler;
  @Mock private AssociationContextHandler declaredExperienceHandler;

  private AssociationServiceImpl service;

  @BeforeEach
  void setUp() {
    lenient().when(traceHandler.getContextType()).thenReturn(EAssociationContextType.TRACE);
    lenient()
        .when(declaredActivityHandler.getContextType())
        .thenReturn(EAssociationContextType.DECLARED_ACTIVITY);
    lenient()
        .when(declaredSkillHandler.getContextType())
        .thenReturn(EAssociationContextType.DECLARED_SKILL);
    lenient()
        .when(declaredExperienceHandler.getContextType())
        .thenReturn(EAssociationContextType.DECLARED_EXPERIENCE);

    service =
        new AssociationServiceImpl(
            associationRepository,
            List.of(
                traceHandler,
                declaredActivityHandler,
                declaredSkillHandler,
                declaredExperienceHandler));
  }

  private static Association association(UUID id1, UUID id2, EAssociationType associationType) {
    return Association.create(id1, id2, associationType);
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
  void getAllAssociatedElementsOf_should_merge_what_every_context_handler_returns() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    var declaredActivity = mock(DeclaredActivity.class);
    var declaredSkillProgress = mock(DeclaredSkillProgress.class);

    var declaredActivityAssociation =
        association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE);
    var declaredSkillAssociation =
        association(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL);

    givenAssociationsOf(
        traceId, Trace.class, List.of(declaredActivityAssociation, declaredSkillAssociation));

    when(declaredActivityHandler.toAssociatedElements(
            List.of(declaredActivityAssociation), Trace.class, false))
        .thenReturn(
            AssociatedElementsData.ofDeclaredActivities(
                List.of(
                    new DeclaredActivityAssociationData(
                        declaredActivityAssociation.getId(),
                        declaredActivity,
                        EDeclaredActivityStatus.IN_PROGRESS))));
    when(declaredSkillHandler.toAssociatedElements(
            List.of(declaredSkillAssociation), Trace.class, false))
        .thenReturn(
            AssociatedElementsData.ofDeclaredSkills(
                List.of(
                    new DeclaredSkillAssociationData(
                        declaredSkillAssociation.getId(), declaredSkillProgress))));

    var result = service.getAllAssociatedElementsOf(traceId, EAssociationContextType.TRACE, false);

    verify(traceHandler).checkLoggedInStudentOwns(List.of(traceId));
    assertThat(result.traceAssociations()).isEmpty();
    assertThat(result.declaredExperienceAssociations()).isEmpty();
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
  }

  @Test
  void getAllAssociatedElementsOf_should_ask_the_handlers_for_the_not_completed_elements_only() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivityAssociation =
        association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE);

    givenAssociationsOf(traceId, Trace.class, List.of(declaredActivityAssociation));
    when(declaredActivityHandler.toAssociatedElements(
            List.of(declaredActivityAssociation), Trace.class, true))
        .thenReturn(AssociatedElementsData.empty());

    service.getAllAssociatedElementsOf(traceId, EAssociationContextType.TRACE, true);

    verify(declaredActivityHandler)
        .toAssociatedElements(List.of(declaredActivityAssociation), Trace.class, true);
  }

  @Test
  void getAllAssociatedElementsOf_should_not_call_any_handler_when_there_is_no_association() {
    UUID traceId = UUID.randomUUID();

    givenAssociationsOf(traceId, Trace.class, List.of());

    var result = service.getAllAssociatedElementsOf(traceId, EAssociationContextType.TRACE, false);

    assertThat(result).isEqualTo(AssociatedElementsData.empty());
    verify(declaredActivityHandler, never()).toAssociatedElements(anyList(), any(), anyBoolean());
    verify(declaredSkillHandler, never()).toAssociatedElements(anyList(), any(), anyBoolean());
    verify(declaredExperienceHandler, never()).toAssociatedElements(anyList(), any(), anyBoolean());
  }

  @Test
  void associate_should_check_both_sides_before_creating_the_associations() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    when(associationRepository.findAllIn(anyList())).thenReturn(List.of());
    when(associationRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);
    givenAssociationsOf(traceId, Trace.class, List.of());

    service.associate(
        traceId,
        EAssociationContextType.TRACE,
        EAssociationContextType.DECLARED_SKILL,
        List.of(declaredSkillProgressId));

    verify(traceHandler)
        .checkLoggedInStudentCanAssociate(traceId, EAssociationType.TRACE_DECLARED_SKILL, 1);
    verify(declaredSkillHandler).checkLoggedInStudentOwns(List.of(declaredSkillProgressId));
    verify(associationRepository)
        .findAllIn(
            List.of(
                new AssociationData(
                    traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL)));
    verify(associationRepository).saveAll(anyList());
  }

  @Test
  void associate_should_create_the_associations_when_the_element_is_the_second_key() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    when(associationRepository.findAllIn(anyList())).thenReturn(List.of());
    when(associationRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);
    givenAssociationsOf(traceId, Trace.class, List.of());

    service.associate(
        traceId,
        EAssociationContextType.TRACE,
        EAssociationContextType.DECLARED_ACTIVITY,
        List.of(declaredActivityId));

    verify(associationRepository)
        .findAllIn(
            List.of(
                new AssociationData(
                    declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE)));
  }

  @Test
  void associate_should_create_a_single_association_for_a_duplicated_id() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    when(associationRepository.findAllIn(anyList())).thenReturn(List.of());
    when(associationRepository.saveAll(anyList())).thenAnswer(i -> i.getArguments()[0]);
    givenAssociationsOf(traceId, Trace.class, List.of());

    service.associate(
        traceId,
        EAssociationContextType.TRACE,
        EAssociationContextType.DECLARED_SKILL,
        List.of(declaredSkillProgressId, declaredSkillProgressId));

    verify(traceHandler)
        .checkLoggedInStudentCanAssociate(traceId, EAssociationType.TRACE_DECLARED_SKILL, 1);
    verify(associationRepository)
        .findAllIn(
            List.of(
                new AssociationData(
                    traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL)));
  }

  @Test
  void associate_should_not_create_anything_when_an_associated_element_is_not_owned() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    doThrow(new UserNotAuthorizedException())
        .when(declaredSkillHandler)
        .checkLoggedInStudentOwns(List.of(declaredSkillProgressId));

    assertThatThrownBy(
            () ->
                service.associate(
                    traceId,
                    EAssociationContextType.TRACE,
                    EAssociationContextType.DECLARED_SKILL,
                    List.of(declaredSkillProgressId)))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(associationRepository, never()).saveAll(anyList());
  }

  @Test
  void associate_should_throw_when_the_two_contexts_cannot_be_associated() {
    UUID traceId = UUID.randomUUID();

    assertThatThrownBy(
            () ->
                service.associate(
                    traceId,
                    EAssociationContextType.TRACE,
                    EAssociationContextType.TRACE,
                    List.of(UUID.randomUUID())))
        .isInstanceOf(WrongClassTypeArgumentException.class);

    verifyNoInteractions(associationRepository);
  }

  @Test
  void unassociate_should_delete_only_the_given_associations() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    var declaredSkillAssociation =
        association(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL);
    var declaredExperienceAssociation =
        association(traceId, UUID.randomUUID(), EAssociationType.TRACE_DECLARED_EXPERIENCE);

    givenAssociationsOf(
        traceId, Trace.class, List.of(declaredSkillAssociation, declaredExperienceAssociation));
    when(associationRepository.findAllById(List.of(declaredSkillAssociation.getId())))
        .thenReturn(List.of(declaredSkillAssociation));

    service.unassociate(
        traceId, EAssociationContextType.TRACE, List.of(declaredSkillAssociation.getId()));

    verify(traceHandler).checkLoggedInStudentCanUnassociate(List.of(traceId));
    verify(declaredSkillHandler)
        .checkLoggedInStudentCanUnassociate(List.of(declaredSkillProgressId));
    verify(declaredExperienceHandler, never()).checkLoggedInStudentCanUnassociate(anyList());
    verify(associationRepository).removeAllFromDatabase(List.of(declaredSkillAssociation));
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
                    traceId,
                    EAssociationContextType.TRACE,
                    List.of(traceAssociation.getId(), otherAssociationId)))
        .isInstanceOf(AssociationDoesNotExistException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void unassociate_should_not_delete_anything_when_an_element_cannot_be_unassociated() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();

    var declaredActivityAssociation =
        association(declaredActivityId, traceId, EAssociationType.DECLARED_ACTIVITY_TRACE);

    givenAssociationsOf(traceId, Trace.class, List.of(declaredActivityAssociation));
    doThrow(new UserNotAuthorizedException())
        .when(declaredActivityHandler)
        .checkLoggedInStudentCanUnassociate(List.of(declaredActivityId));

    assertThatThrownBy(
            () ->
                service.unassociate(
                    traceId,
                    EAssociationContextType.TRACE,
                    List.of(declaredActivityAssociation.getId())))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(associationRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void searchForAssociation_should_disable_the_elements_already_associated_with_the_element() {
    UUID declaredSkillProgressId = UUID.randomUUID();
    UUID associatedTraceId = UUID.randomUUID();
    UUID availableTraceId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    when(associationRepository.findAllOf(
            declaredSkillProgressId,
            DeclaredSkillProgress.class,
            List.of(EAssociationType.TRACE_DECLARED_SKILL)))
        .thenReturn(
            List.of(
                association(
                    associatedTraceId,
                    declaredSkillProgressId,
                    EAssociationType.TRACE_DECLARED_SKILL)));
    when(traceHandler.search("kw", AssociationSearchFilter.NONE, pageCriteria))
        .thenReturn(
            new PagedResult<>(
                List.of(
                    new AssociationSearchResultData(associatedTraceId, "associated", null, false),
                    new AssociationSearchResultData(availableTraceId, "available", null, false)),
                new PageInfo(0, 10, 2)));

    var result =
        service.searchForAssociation(
            declaredSkillProgressId,
            EAssociationContextType.DECLARED_SKILL,
            EAssociationContextType.TRACE,
            "kw",
            AssociationSearchFilter.NONE,
            pageCriteria);

    verify(declaredSkillHandler).checkLoggedInStudentOwns(List.of(declaredSkillProgressId));
    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(associatedTraceId, "associated", null, true),
            new AssociationSearchResultData(availableTraceId, "available", null, false));
  }

  @Test
  void searchForAssociation_should_keep_the_elements_disabled_by_their_own_context() {
    UUID traceId = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    when(associationRepository.findAllOf(
            traceId, Trace.class, List.of(EAssociationType.DECLARED_ACTIVITY_TRACE)))
        .thenReturn(List.of());
    when(declaredActivityHandler.search("kw", AssociationSearchFilter.NONE, pageCriteria))
        .thenReturn(
            new PagedResult<>(
                List.of(
                    new AssociationSearchResultData(
                        declaredActivityId, "Activity", "EXPERIENCES", true)),
                new PageInfo(0, 10, 1)));

    var result =
        service.searchForAssociation(
            traceId,
            EAssociationContextType.TRACE,
            EAssociationContextType.DECLARED_ACTIVITY,
            "kw",
            AssociationSearchFilter.NONE,
            pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(declaredActivityId, "Activity", "EXPERIENCES", true));
  }

  @Test
  void searchForAssociation_should_pass_the_filter_to_the_searched_context() {
    UUID declaredSkillProgressId = UUID.randomUUID();
    UUID associatedTraceId = UUID.randomUUID();
    var filter = new TraceAssociationSearchFilter(true);
    var pageCriteria = new PageCriteria(0, 10);

    when(associationRepository.findAllOf(
            declaredSkillProgressId,
            DeclaredSkillProgress.class,
            List.of(EAssociationType.TRACE_DECLARED_SKILL)))
        .thenReturn(List.of());
    when(traceHandler.search("kw", filter, pageCriteria))
        .thenReturn(
            new PagedResult<>(
                List.of(
                    new AssociationSearchResultData(associatedTraceId, "associated", null, false)),
                new PageInfo(0, 10, 1)));

    var result =
        service.searchForAssociation(
            declaredSkillProgressId,
            EAssociationContextType.DECLARED_SKILL,
            EAssociationContextType.TRACE,
            "kw",
            filter,
            pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(associatedTraceId, "associated", null, false));
  }

  @Test
  void searchForAssociation_should_throw_when_the_context_cannot_be_associated_with_the_element() {
    UUID traceId = UUID.randomUUID();

    assertThatThrownBy(
            () ->
                service.searchForAssociation(
                    traceId,
                    EAssociationContextType.TRACE,
                    EAssociationContextType.TRACE,
                    "kw",
                    AssociationSearchFilter.NONE,
                    new PageCriteria(0, 10)))
        .isInstanceOf(WrongClassTypeArgumentException.class);

    verifyNoInteractions(associationRepository);
  }

  @Test
  void searchForAssociationWithNewElement_should_return_the_results_of_the_searched_context() {
    UUID declaredSkillProgressId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    when(declaredSkillHandler.search("kw", AssociationSearchFilter.NONE, pageCriteria))
        .thenReturn(
            new PagedResult<>(
                List.of(
                    new AssociationSearchResultData(
                        declaredSkillProgressId, "Declared skill", "Referential", false)),
                new PageInfo(0, 10, 1)));

    var result =
        service.searchForAssociationWithNewElement(
            EAssociationContextType.DECLARED_EXPERIENCE,
            EAssociationContextType.DECLARED_SKILL,
            "kw",
            AssociationSearchFilter.NONE,
            pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                declaredSkillProgressId, "Declared skill", "Referential", false));
    assertThat(result.pageInfo()).isEqualTo(new PageInfo(0, 10, 1));
    verifyNoInteractions(associationRepository);
  }

  @Test
  void searchForAssociationWithNewElement_should_keep_the_elements_disabled_by_their_own_context() {
    UUID declaredActivityId = UUID.randomUUID();
    var pageCriteria = new PageCriteria(0, 10);

    when(declaredActivityHandler.search(null, AssociationSearchFilter.NONE, pageCriteria))
        .thenReturn(
            new PagedResult<>(
                List.of(
                    new AssociationSearchResultData(
                        declaredActivityId, "Finished activity", "EXPERIENCES", true)),
                new PageInfo(0, 10, 1)));

    var result =
        service.searchForAssociationWithNewElement(
            EAssociationContextType.TRACE,
            EAssociationContextType.DECLARED_ACTIVITY,
            null,
            AssociationSearchFilter.NONE,
            pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(
                declaredActivityId, "Finished activity", "EXPERIENCES", true));
  }

  @Test
  void searchForAssociationWithNewElement_should_pass_the_filter_to_the_searched_context() {
    UUID unassociatedTraceId = UUID.randomUUID();
    var filter = new TraceAssociationSearchFilter(false);
    var pageCriteria = new PageCriteria(0, 10);

    when(traceHandler.search("kw", filter, pageCriteria))
        .thenReturn(
            new PagedResult<>(
                List.of(
                    new AssociationSearchResultData(
                        unassociatedTraceId, "unassociated", null, false)),
                new PageInfo(0, 10, 1)));

    var result =
        service.searchForAssociationWithNewElement(
            EAssociationContextType.DECLARED_SKILL,
            EAssociationContextType.TRACE,
            "kw",
            filter,
            pageCriteria);

    assertThat(result.content())
        .containsExactly(
            new AssociationSearchResultData(unassociatedTraceId, "unassociated", null, false));
  }

  @Test
  void searchForAssociationWithNewElement_should_not_check_the_ownership_of_the_new_element() {
    var pageCriteria = new PageCriteria(0, 10);

    when(traceHandler.search("kw", AssociationSearchFilter.NONE, pageCriteria))
        .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 10, 0)));

    service.searchForAssociationWithNewElement(
        EAssociationContextType.DECLARED_SKILL,
        EAssociationContextType.TRACE,
        "kw",
        AssociationSearchFilter.NONE,
        pageCriteria);

    verify(declaredSkillHandler, never()).checkLoggedInStudentOwns(anyList());
  }

  @Test
  void
      searchForAssociationWithNewElement_should_throw_when_the_two_contexts_cannot_be_associated() {
    assertThatThrownBy(
            () ->
                service.searchForAssociationWithNewElement(
                    EAssociationContextType.DECLARED_ACTIVITY,
                    EAssociationContextType.DECLARED_EXPERIENCE,
                    "kw",
                    AssociationSearchFilter.NONE,
                    new PageCriteria(0, 10)))
        .isInstanceOf(WrongClassTypeArgumentException.class);

    verify(declaredExperienceHandler, never()).search(any(), any(), any());
    verifyNoInteractions(associationRepository);
  }
}
