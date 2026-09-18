package fr.avenirsesr.portfolio.student.association.domain.utils;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.student.association.domain.model.Association;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public interface AssociationUtils {
  static List<UUID> getIdsOf(
      List<Association> associations, EAssociationType associationType, Class<?> clazz) {
    return associations.stream()
        .filter(a -> a.getAssociationType() == associationType)
        .map(associationType.idExtractorFor(clazz))
        .toList();
  }

  static <T extends AvenirsBaseModel> void checkOwnership(
      List<UUID> ids,
      List<T> elements,
      Function<T, Student> studentExtractor,
      Student loggedInStudent,
      Supplier<RuntimeException> notFoundException) {
    if (!new HashSet<>(elements.stream().map(AvenirsBaseModel::getId).toList()).containsAll(ids)) {
      throw notFoundException.get();
    }

    if (!elements.stream()
        .allMatch(element -> studentExtractor.apply(element).equals(loggedInStudent))) {
      throw new UserNotAuthorizedException();
    }
  }

  static <T extends AvenirsBaseModel> T elementOf(
      List<T> elements, UUID id, Supplier<RuntimeException> notFoundException) {
    return elements.stream()
        .filter(element -> element.getId().equals(id))
        .findAny()
        .orElseThrow(notFoundException);
  }
}
