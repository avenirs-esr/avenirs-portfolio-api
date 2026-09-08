package fr.avenirsesr.portfolio.student.association.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.student.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionalAssociationService implements AssociationService {
  private final AssociationService delegate;

  @Override
  public List<Association> createAll(List<AssociationData> associationsData) {
    return delegate.createAll(associationsData);
  }

  @Override
  public List<Association> getAllOf(
      UUID id, Class<?> clazz, List<EAssociationType> associationTypes) {
    return delegate.getAllOf(id, clazz, associationTypes);
  }

  @Override
  public List<Association> getAllOf(
      List<UUID> ids, Class<?> clazz, List<EAssociationType> associationTypes) {
    return delegate.getAllOf(ids, clazz, associationTypes);
  }

  @Override
  public Map<UUID, Long> countAllOf(
      List<UUID> ids, Class<?> clazz, EAssociationType associationType) {
    return delegate.countAllOf(ids, clazz, associationType);
  }

  @Override
  public void deleteAllByIds(List<UUID> ids) {
    delegate.deleteAllByIds(ids);
  }

  @Override
  public void deleteAllOf(List<UUID> ids, Class<?> clazz) {
    delegate.deleteAllOf(ids, clazz);
  }

  @Transactional
  @Override
  public void deleteAssociationsOf(Class<?> subjectClass, UUID subjectId) {
    delegate.deleteAssociationsOf(subjectClass, subjectId);
  }
}
