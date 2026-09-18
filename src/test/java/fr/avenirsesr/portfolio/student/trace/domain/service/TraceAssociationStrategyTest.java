package fr.avenirsesr.portfolio.student.trace.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.filter.TraceAssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceViewData;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceAssociationStrategyTest {

  @Mock private TraceService traceService;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private TraceAssociationStrategy strategy;

  private Trace traceOf(UUID id, Student student) {
    var trace = mock(Trace.class);
    when(trace.getId()).thenReturn(id);
    when(trace.getStudent()).thenReturn(student);

    return trace;
  }

  @Test
  void getContextType_should_return_the_trace_context() {
    assertThat(strategy.getContextType()).isEqualTo(EAssociationContextType.TRACE);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_a_trace_does_not_exist() {
    UUID traceId = UUID.randomUUID();

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of());

    assertThatThrownBy(() -> strategy.checkLoggedInStudentOwns(List.of(traceId)))
        .isInstanceOf(TraceNotFoundException.class);
  }

  @Test
  void checkLoggedInStudentOwns_should_throw_when_a_trace_belongs_to_another_student() {
    UUID traceId = UUID.randomUUID();

    var trace = traceOf(traceId, StudentFixture.create().toModel());

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(StudentFixture.create().toModel());

    assertThatThrownBy(() -> strategy.checkLoggedInStudentOwns(List.of(traceId)))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void checkLoggedInStudentCanAssociate_should_not_throw_when_the_traces_are_owned() {
    var student = StudentFixture.create().toModel();
    UUID traceId = UUID.randomUUID();

    var trace = traceOf(traceId, student);

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    strategy.checkLoggedInStudentCanAssociate(traceId, EAssociationType.TRACE_DECLARED_SKILL, 1);
  }

  private UUID givenTracesViewFilteredOn(Boolean isAssociated, PageCriteria pageCriteria) {
    UUID traceId = UUID.randomUUID();

    when(traceService.getTracesView(
            "kw",
            new TraceFilter(isAssociated, null, null, null),
            null,
            pageCriteria,
            new SortCriteria(ESortField.DATE, ESortOrder.DESC)))
        .thenReturn(
            new PagedResult<>(
                List.of(
                    new TraceViewData(
                        traceId,
                        "My trace",
                        Boolean.TRUE.equals(isAssociated),
                        null,
                        null,
                        Optional.empty(),
                        Optional.empty(),
                        null,
                        null,
                        null)),
                new PageInfo(0, 10, 1)));

    return traceId;
  }

  @Test
  void search_should_return_the_traces_of_the_logged_in_student() {
    var pageCriteria = new PageCriteria(0, 10);
    UUID traceId = givenTracesViewFilteredOn(null, pageCriteria);

    var result = strategy.search("kw", AssociationSearchFilter.NONE, pageCriteria);

    assertThat(result.content())
        .containsExactly(new AssociationSearchResultData(traceId, "My trace", null, false));
  }

  @Test
  void search_should_return_only_the_associated_traces_when_filtering_on_associated_traces() {
    var pageCriteria = new PageCriteria(0, 10);
    UUID traceId = givenTracesViewFilteredOn(true, pageCriteria);

    var result = strategy.search("kw", new TraceAssociationSearchFilter(true), pageCriteria);

    assertThat(result.content())
        .extracting(AssociationSearchResultData::id)
        .containsExactly(traceId);
  }

  @Test
  void search_should_return_only_the_unassociated_traces_when_filtering_on_unassociated_traces() {
    var pageCriteria = new PageCriteria(0, 10);
    UUID traceId = givenTracesViewFilteredOn(false, pageCriteria);

    var result = strategy.search("kw", new TraceAssociationSearchFilter(false), pageCriteria);

    assertThat(result.content())
        .extracting(AssociationSearchResultData::id)
        .containsExactly(traceId);
  }

  @Test
  void search_should_not_filter_on_the_association_when_the_trace_filter_is_empty() {
    var pageCriteria = new PageCriteria(0, 10);
    UUID traceId = givenTracesViewFilteredOn(null, pageCriteria);

    var result = strategy.search("kw", new TraceAssociationSearchFilter(null), pageCriteria);

    assertThat(result.content())
        .extracting(AssociationSearchResultData::id)
        .containsExactly(traceId);
  }

  @Test
  void toAssociatedElements_should_return_the_traces_of_the_associations() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    var trace = mock(Trace.class);
    when(trace.getId()).thenReturn(traceId);

    var association =
        Association.create(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL);

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));

    var result =
        strategy.toAssociatedElements(List.of(association), DeclaredSkillProgress.class, false);

    assertThat(result.traceAssociations())
        .singleElement()
        .satisfies(
            data -> {
              assertThat(data.associationId()).isEqualTo(association.getId());
              assertThat(data.trace()).isEqualTo(trace);
            });
  }

  @Test
  void toAssociatedElements_should_throw_when_a_trace_is_missing() {
    UUID traceId = UUID.randomUUID();
    UUID declaredSkillProgressId = UUID.randomUUID();

    var association =
        Association.create(traceId, declaredSkillProgressId, EAssociationType.TRACE_DECLARED_SKILL);

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of());

    assertThatThrownBy(
            () ->
                strategy.toAssociatedElements(
                    List.of(association), DeclaredSkillProgress.class, false))
        .isInstanceOf(TraceNotFoundException.class);
  }
}
