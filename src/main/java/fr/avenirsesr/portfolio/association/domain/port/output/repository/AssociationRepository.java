package fr.avenirsesr.portfolio.association.domain.port.output.repository;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AssociationRepository extends GenericRepositoryPort<Association> {
  List<Association> findAllIn(List<AssociationData> associations);

  List<Association> findAllOf(UUID id, Class<?> clazz, List<EAssociationType> associationTypes);

  List<Association> findAllOf(List<EAssociationType> associationTypes);

  List<Association> findAllByIds(
      Set<UUID> ids, Class<?> clazz, List<EAssociationType> associationTypes);
}
