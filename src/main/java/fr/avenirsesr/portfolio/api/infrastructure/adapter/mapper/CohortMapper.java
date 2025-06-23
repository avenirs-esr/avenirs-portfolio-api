package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Cohort;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.CohortEntity;
import java.util.stream.Collectors;

public interface CohortMapper {
  static CohortEntity fromDomain(Cohort cohort) {

    return new CohortEntity(
        cohort.getId(),
        cohort.getName(),
        cohort.getDescription(),
        cohort.getUsers().stream().map(UserMapper::fromDomain).collect(Collectors.toSet()),
        ProgramProgressMapper.fromDomain(cohort.getProgramProgress()),
        cohort.getAmsSet().stream().map(AMSMapper::fromDomain).collect(Collectors.toSet()));
  }

  static Cohort toDomain(CohortEntity entity) {
    return toDomain(entity, ELanguage.FRENCH);
  }

  static Cohort toDomain(CohortEntity entity, ELanguage language) {
    Cohort cohort = toDomainWithoutRecursion(entity, language);

    if (!entity.getAmsEntities().isEmpty()) {
      cohort.setAmsSet(
          entity.getAmsEntities().stream()
              .map(amsEntity -> AMSMapper.toDomainWithoutRecursion(amsEntity, language))
              .collect(Collectors.toSet()));
    }

    return cohort;
  }

  static Cohort toDomainWithoutRecursion(CohortEntity entity, ELanguage language) {
    return Cohort.toDomain(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        ProgramProgressMapper.toDomain(entity.getProgramProgress(), language),
        entity.getUsers().stream().map(UserMapper::toDomain).collect(Collectors.toSet()),
        java.util.Collections.emptySet());
  }
}
