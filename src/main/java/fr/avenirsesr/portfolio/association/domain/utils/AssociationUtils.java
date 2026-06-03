package fr.avenirsesr.portfolio.association.domain.utils;

import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import java.util.List;
import java.util.UUID;

public interface AssociationUtils {
  static List<UUID> getIdsOf(
      List<Association> associations, EAssociationType associationType, Class<?> clazz) {
    return associations.stream()
        .filter(a -> a.getAssociationType() == associationType)
        .map(associationType.idExtractorFor(clazz))
        .toList();
  }
}
