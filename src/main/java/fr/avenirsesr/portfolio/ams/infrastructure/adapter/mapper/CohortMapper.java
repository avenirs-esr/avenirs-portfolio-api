package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
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
    Cohort cohort = toDomainWithoutRecursion(entity);

    if (!entity.getAmsEntities().isEmpty()) {
      cohort.setAmsSet(
          entity.getAmsEntities().stream()
              .map(AMSMapper::toDomainWithoutRecursion)
              .collect(Collectors.toSet()));
    }

    return cohort;
  }

  static Cohort toDomainWithoutRecursion(CohortEntity entity) {
    return Cohort.toDomain(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        ProgramProgressMapper.toDomain(entity.getProgramProgress()),
        entity.getUsers().stream().map(UserMapper::toDomain).collect(Collectors.toSet()),
        java.util.Collections.emptySet());
  }
}
