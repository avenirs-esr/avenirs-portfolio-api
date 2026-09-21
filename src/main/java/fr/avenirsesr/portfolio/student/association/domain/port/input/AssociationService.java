package fr.avenirsesr.portfolio.student.association.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssociationService {
  List<Association> createAll(List<AssociationData> associationsData);

  List<Association> getAllOf(UUID id, Class<?> clazz, List<EAssociationType> associationTypes);

  List<Association> getAllOf(
      List<UUID> ids, Class<?> clazz, List<EAssociationType> associationTypes);

  Map<UUID, Long> countAllOf(List<UUID> ids, Class<?> clazz, EAssociationType associationType);

  AssociatedElementsData getAllAssociatedElementsOf(
      UUID id, EAssociationContextType contextType, boolean onlyNotCompleted);

  AssociatedElementsData associate(
      UUID id,
      EAssociationContextType contextType,
      EAssociationContextType associatedContextType,
      List<UUID> associatedIds);

  void unassociate(UUID id, EAssociationContextType contextType, List<UUID> associationIds);

  PagedResult<AssociationSearchResultData> searchForAssociation(
      UUID id,
      EAssociationContextType contextType,
      EAssociationContextType associatedContextType,
      String keyword,
      AssociationSearchFilter filter,
      PageCriteria pageCriteria);

  PagedResult<AssociationSearchResultData> searchForAssociationWithNewElement(
      EAssociationContextType contextType,
      EAssociationContextType associatedContextType,
      String keyword,
      AssociationSearchFilter filter,
      PageCriteria pageCriteria);

  void deleteAllByIds(List<UUID> ids);

  void deleteAllOf(List<UUID> ids, Class<?> clazz);
}
