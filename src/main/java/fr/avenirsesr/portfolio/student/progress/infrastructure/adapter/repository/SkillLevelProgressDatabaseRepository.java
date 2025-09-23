package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification.SkillLevelProgressSpecification;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class SkillLevelProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<SkillLevelProgress, SkillLevelProgressEntity>
    implements SkillLevelProgressRepository {
  private final SkillLevelProgressJpaRepository jpaRepository;

  public SkillLevelProgressDatabaseRepository(SkillLevelProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        SkillLevelProgressMapper::fromDomain,
        SkillLevelProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<SkillLevelProgress> linkedWith(AMS ams) {
    return jpaSpecificationExecutor
        .findAll(SkillLevelProgressSpecification.ofAms(AMSMapper.fromDomain(ams)))
        .stream()
        .map(SkillLevelProgressMapper::toDomain)
        .toList();
  }
}
