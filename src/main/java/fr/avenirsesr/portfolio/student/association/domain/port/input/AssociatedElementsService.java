package fr.avenirsesr.portfolio.student.association.domain.port.input;

import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import java.util.UUID;

public interface AssociatedElementsService {
  AssociatedElementsData getAllAssociatedElementsOf(UUID id, Class<?> clazz);

  AssociatedElementsData getAllAssociatedElementsOf(
      UUID id, Class<?> clazz, boolean onlyNotCompletedActivities);
}
