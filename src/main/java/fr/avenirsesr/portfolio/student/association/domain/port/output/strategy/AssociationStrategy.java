package fr.avenirsesr.portfolio.student.association.domain.port.output.strategy;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import java.util.List;
import java.util.UUID;

public interface AssociationStrategy {
  EAssociationContextType getContextType();

  void checkLoggedInStudentOwns(List<UUID> elementIds);

  void checkLoggedInStudentCanAssociate(
      UUID elementId, EAssociationType associationType, int associationsToAdd);

  void checkLoggedInStudentCanUnassociate(List<UUID> elementIds);

  PagedResult<AssociationSearchResultData> search(
      String keyword, AssociationSearchFilter filter, PageCriteria pageCriteria);

  AssociatedElementsData toAssociatedElements(
      List<Association> associations, Class<?> subjectClass, boolean onlyNotCompleted);
}
