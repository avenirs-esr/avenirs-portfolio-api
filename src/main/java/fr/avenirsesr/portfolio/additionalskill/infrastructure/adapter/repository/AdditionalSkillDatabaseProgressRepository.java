package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import static fr.avenirsesr.portfolio.shared.application.adapter.utils.PaginationUtils.paginate;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.specification.AdditionalSkillProgressSpecification;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillDatabaseProgressRepository
    extends GenericJpaRepositoryAdapter<AdditionalSkillProgress, AdditionalSkillProgressEntity>
    implements AdditionalSkillProgressRepository {
  private final AdditionalSkillProgressJpaRepository jpaRepository;
  private final AdditionalSkillCache additionalSkillCache;

  public AdditionalSkillDatabaseProgressRepository(
      AdditionalSkillProgressJpaRepository jpaRepository,
      AdditionalSkillCache additionalSkillCache) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillProgressMapper::fromDomain,
        AdditionalSkillProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
    this.additionalSkillCache = additionalSkillCache;
  }

  public void saveAllEntities(List<AdditionalSkillProgressEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  @Override
  public boolean additionalSkillProgressAlreadyExists(
      AdditionalSkillProgress additionalSkillProgress) {
    return jpaRepository.exists(
        AdditionalSkillProgressSpecification.additionalSkillProgressAlreadyExists(
            additionalSkillProgress.getSkill().getId(),
            additionalSkillProgress.getStudent().getId()));
  }

  @Override
  public PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria) {
    List<AdditionalSkillProgressEntity> entities =
        jpaRepository.findAll(
            AdditionalSkillProgressSpecification.findAllByStudent(student.getId()));

    List<AdditionalSkill> additionalSkills =
        additionalSkillCache.findAllByIds(
            entities.stream().map(AdditionalSkillProgressEntity::getAdditionalSkillId).toList());

    List<AdditionalSkillProgress> progresses =
        entities.stream()
            .map(
                entity ->
                    AdditionalSkillProgressMapper.toDomain(
                        entity,
                        additionalSkills.stream()
                            .filter(
                                additionalSkill ->
                                    additionalSkill.getId().equals(entity.getAdditionalSkillId()))
                            .findFirst()
                            .orElseThrow(AdditionalSkillNotFoundException::new)))
            .toList();
    return paginate(progresses, pageCriteria);
  }
}
