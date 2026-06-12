package fr.avenirsesr.portfolio.association.domain.service;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.AssociationRepository;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssociationServiceImpl implements AssociationService {
  private final AssociationRepository associationRepository;

  @Override
  public List<Association> createAll(List<AssociationData> associationsData) {
    if (!associationRepository.findAllIn(associationsData).isEmpty()) {
      throw new AssociationAlreadyExistException();
    }

    var associations =
        associationsData.stream()
            .map(
                association ->
                    Association.create(
                        association.id1(), association.id2(), association.associationType()))
            .toList();

    return associationRepository.saveAll(associations);
  }

  @Override
  public List<Association> getAllOf(
      UUID id, Class<?> clazz, List<EAssociationType> associationTypes) {
    return associationRepository.findAllOf(id, clazz, associationTypes);
  }

  @Override
  public List<Association> getAllOf(
      List<UUID> ids, Class<?> clazz, List<EAssociationType> associationTypes) {
    if (ids.isEmpty()) {
      return List.of();
    }

    return associationRepository.findAllOf(ids, clazz, associationTypes);
  }

  @Override
  public void deleteAllByIds(List<UUID> ids) {
    var activities = associationRepository.findAllById(ids);

    if (!new HashSet<>(activities.stream().map(Association::getId).toList()).containsAll(ids)) {
      throw new AssociationDoesNotExistException();
    }

    associationRepository.removeAllFromDatabase(activities);
  }

  @Override
  public void deleteAllOf(List<UUID> ids, Class<?> clazz) {
    var associationTypes = EAssociationType.getAllBy(clazz);
    var associations =
        ids.stream().flatMap(id -> getAllOf(id, clazz, associationTypes).stream()).toList();
    deleteAllByIds(associations.stream().map(Association::getId).toList());
  }
}
