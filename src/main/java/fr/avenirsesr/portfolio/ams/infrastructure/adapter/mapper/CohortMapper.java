package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.stream.Collectors;

public interface CohortMapper {
  static CohortEntity fromDomain(Cohort cohort) {

    return CohortEntity.of(
        cohort.getId(),
        cohort.getName(),
        cohort.getDescription(),
        cohort.getUsers().stream().map(UserMapper::fromDomain).collect(Collectors.toSet()),
        TrainingPathMapper.fromDomain(cohort.getTrainingPath()),
        cohort.getAmsSet().stream().map(AMSMapper::fromDomain).collect(Collectors.toSet()));
  }

  static Cohort toDomain(CohortEntity entity) {
    return Cohort.toDomain(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        TrainingPathMapper.toDomain(entity.getTrainingPath()),
        entity.getUsers().stream().map(UserMapper::toDomain).collect(Collectors.toSet()),
        entity.getAmsEntities().stream().map(AMSMapper::toDomain).collect(Collectors.toSet()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
