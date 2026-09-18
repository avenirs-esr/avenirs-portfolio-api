package fr.avenirsesr.portfolio.student.program.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.output.strategy.AssociationStrategy;
import fr.avenirsesr.portfolio.student.association.domain.utils.AssociationUtils;
import fr.avenirsesr.portfolio.student.program.domain.data.DeclaredProgramAssociationData;
import fr.avenirsesr.portfolio.student.program.domain.exception.DeclaredProgramNotFoundException;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.program.domain.port.input.DeclaredProgramService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeclaredProgramAssociationStrategy implements AssociationStrategy {
  private final DeclaredProgramService declaredProgramService;
  private final LoggedInUserService loggedInUserService;

  @Override
  public EAssociationContextType getContextType() {
    return EAssociationContextType.DECLARED_PROGRAM;
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
      String keyword, AssociationSearchFilter filter, PageCriteria pageCriteria) {
    var declaredPrograms = declaredProgramService.search(keyword, pageCriteria);

    return new PagedResult<>(
        declaredPrograms.content().stream()
            .map(
                declaredProgram ->
                    new AssociationSearchResultData(
                        declaredProgram.getId(),
                        declaredProgram.getTitle(),
                        declaredProgram.getOrganization(),
                        false))
            .toList(),
        declaredPrograms.pageInfo());
  }

  @Override
  public AssociatedElementsData toAssociatedElements(
      List<Association> associations, Class<?> subjectClass, boolean onlyNotCompleted) {
    var declaredPrograms =
        declaredProgramService.findAllByIds(
            associations.stream()
                .map(association -> association.associatedIdOf(subjectClass))
                .toList());

    return AssociatedElementsData.ofDeclaredPrograms(
        associations.stream()
            .map(
                association ->
                    new DeclaredProgramAssociationData(
                        association.getId(),
                        AssociationUtils.elementOf(
                            declaredPrograms,
                            association.associatedIdOf(subjectClass),
                            DeclaredProgramNotFoundException::new)))
            .toList());
  }

  private void fetchAndCheckOwnership(List<UUID> elementIds) {
    var declaredPrograms = declaredProgramService.findAllByIds(elementIds);

    AssociationUtils.checkOwnership(
        elementIds,
        declaredPrograms,
        DeclaredProgram::getStudent,
        loggedInUserService.getLoggedInStudent(),
        DeclaredProgramNotFoundException::new);
  }
}
