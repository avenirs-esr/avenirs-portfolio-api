package fr.avenirsesr.portfolio.student.trace.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.handler.AssociationContextHandler;
import fr.avenirsesr.portfolio.student.association.domain.utils.AssociationUtils;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.input.TraceService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceAssociationContextHandler implements AssociationContextHandler {
  private final TraceService traceService;
  private final LoggedInUserService loggedInUserService;

  @Override
  public EAssociationContextType getContextType() {
    return EAssociationContextType.TRACE;
  }

  @Override
  public void checkLoggedInStudentOwns(List<UUID> elementIds) {
    fetchAndCheckOwnership(elementIds);
  }

  @Override
  public void checkLoggedInStudentCanAssociate(
      UUID elementId, EAssociationType associationType, int associationsToAdd) {
    checkLoggedInStudentOwns(List.of(elementId));
  }

  @Override
  public void checkLoggedInStudentCanUnassociate(List<UUID> elementIds) {
    checkLoggedInStudentOwns(elementIds);
  }

  @Override
  public PagedResult<AssociationSearchResultData> search(
      String keyword, PageCriteria pageCriteria) {
    var traces =
        traceService.getTracesView(
            keyword,
            new TraceFilter(null, null, null, null),
            null,
            pageCriteria,
            new SortCriteria(ESortField.DATE, ESortOrder.DESC));

    return new PagedResult<>(
        traces.content().stream()
            .map(trace -> new AssociationSearchResultData(trace.id(), trace.title(), null, false))
            .toList(),
        traces.pageInfo());
  }

  @Override
  public AssociatedElementsData toAssociatedElements(
      List<Association> associations, Class<?> subjectClass, boolean onlyNotCompleted) {
    var traces =
        traceService.findAllTracesById(
            associations.stream()
                .map(association -> association.associatedIdOf(subjectClass))
                .toList());

    return AssociatedElementsData.ofTraces(
        associations.stream()
            .map(
                association ->
                    new TraceAssociationData(
                        association.getId(),
                        AssociationUtils.elementOf(
                            traces,
                            association.associatedIdOf(subjectClass),
                            TraceNotFoundException::new)))
            .toList());
  }

  private List<Trace> fetchAndCheckOwnership(List<UUID> elementIds) {
    var traces = traceService.findAllTracesById(elementIds);

    AssociationUtils.checkOwnership(
        elementIds,
        traces,
        Trace::getStudent,
        loggedInUserService.getLoggedInStudent(),
        TraceNotFoundException::new);

    return traces;
  }
}
