package fr.avenirsesr.portfolio.student.association.domain.port.input;

import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
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

  void deleteAllByIds(List<UUID> ids);

  void deleteAllOf(List<UUID> ids, Class<?> clazz);
}
