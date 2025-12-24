package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.stream.Collectors;

public class CohortMapper implements Mapper<CohortEntity, Cohort> {
  public static final CohortMapper INSTANCE = new CohortMapper();

  @Override
  public CohortEntity fromDomain(Cohort cohort) {

    return CohortEntity.of(
        cohort.getId(),
        cohort.getName(),
        cohort.getDescription(),
        cohort.getUsers().stream().map(UserMapper.INSTANCE::fromDomain).collect(Collectors.toSet()),
        TrainingPathMapper.INSTANCE.fromDomain(cohort.getTrainingPath()),
        cohort.getAmsSet().stream()
            .map(AMSMapper.INSTANCE::fromDomain)
            .collect(Collectors.toSet()));
  }

  @Override
  public Cohort toDomain(CohortEntity entity) {
    return Cohort.toDomain(
        entity.getId(),
        entity.getName(),
        entity.getDescription(),
        TrainingPathMapper.INSTANCE.toDomain(entity.getTrainingPath()),
        entity.getUsers().stream().map(UserMapper.INSTANCE::toDomain).collect(Collectors.toSet()),
        entity.getAmsEntities().stream()
            .map(AMSMapper.INSTANCE::toDomain)
            .collect(Collectors.toSet()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
