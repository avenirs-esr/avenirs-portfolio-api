package fr.avenirsesr.portfolio.student.association.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
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

  AssociatedElementsData getAllAssociatedElementsOf(UUID id, Class<?> clazz);

  AssociatedElementsData getAllAssociatedElementsOf(
      UUID id, Class<?> clazz, boolean onlyNotCompletedActivities);

  void associate(
      UUID id, Class<?> clazz, List<UUID> associatedIds, EAssociationType associationType);

  void unassociate(UUID id, Class<?> clazz, List<UUID> associationIds);

  PagedResult<AssociationSearchResultData> searchForAssociation(
      UUID id,
      Class<?> clazz,
      EAssociationContextType contextType,
      String keyword,
      PageCriteria pageCriteria);

  void deleteAllByIds(List<UUID> ids);

  void deleteAllOf(List<UUID> ids, Class<?> clazz);
}
