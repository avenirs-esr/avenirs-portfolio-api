package fr.avenirsesr.portfolio.association.domain.port.input;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import java.util.List;
import java.util.UUID;

public interface AssociationService {
  List<Association> createAll(List<AssociationData> associationsData);

  List<Association> getAllOf(UUID id, Class<?> clazz, List<EAssociationType> associationTypes);

  void deleteAllByIds(List<UUID> ids);
}
