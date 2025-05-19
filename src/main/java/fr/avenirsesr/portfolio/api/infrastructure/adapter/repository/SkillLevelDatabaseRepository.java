package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.SkillLevelRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillLevelEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkillLevelDatabaseRepository implements SkillLevelRepository {
  private final SkillLevelJpaRepository jpaRepository;

  @Override
  public void save(SkillLevel skillLevel) {
    var entity = toEntity(skillLevel);
    jpaRepository.save(entity);
  }

  @Override
  public void saveAll(List<SkillLevel> skillLevels) {
    var entities = skillLevels.stream().map(SkillLevelDatabaseRepository::toEntity).toList();
    jpaRepository.saveAll(entities);
  }

  public static SkillLevelEntity toEntity(SkillLevel skillLevel) {
    return new SkillLevelEntity(
        skillLevel.getId(),
        skillLevel.getName(),
        skillLevel.getStatus(),
        skillLevel.getTraces().stream().map(TraceDatabaseRepository::toEntity).toList(),
        skillLevel.getAmses().stream().map(AMSDatabaseRepository::toEntity).toList());
  }
}
