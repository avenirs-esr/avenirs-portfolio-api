package fr.avenirsesr.portfolio.student.association.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssociationRepository extends GenericRepositoryPort<Association> {
  List<Association> findAllIn(List<AssociationData> associations);

  List<Association> findAllOf(UUID id, Class<?> clazz, List<EAssociationType> associationTypes);

  List<Association> findAllOf(List<EAssociationType> associationTypes);

  List<Association> findAllOf(
      List<UUID> ids, Class<?> clazz, List<EAssociationType> associationTypes);

  Map<UUID, Long> countAllOf(List<UUID> ids, Class<?> clazz, EAssociationType associationType);
}
