package fr.avenirsesr.portfolio.association.domain.service;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationAlreadyExistException;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.port.output.repository.AssociationRepository;
import fr.avenirsesr.portfolio.trace.domain.exception.AssociationDoesNotExistException;
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
  public List<Association> getAllOf(UUID id, Class<?> clazz, EAssociationType associationType) {
    return associationRepository.findAllOf(id, clazz, associationType);
  }

  @Override
  public void deleteAllByIds(List<UUID> ids) {
    var activities = associationRepository.findAllById(ids);

    if (!new HashSet<>(activities.stream().map(Association::getId).toList()).containsAll(ids)) {
      throw new AssociationDoesNotExistException();
    }

    associationRepository.removeAllFromDatabase(activities);
  }
}
