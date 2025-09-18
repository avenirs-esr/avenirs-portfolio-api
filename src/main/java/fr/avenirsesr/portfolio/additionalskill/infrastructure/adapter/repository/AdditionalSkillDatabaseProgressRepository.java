package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.specification.AdditionalSkillProgressSpecification;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionalSkillDatabaseProgressRepository
    extends GenericJpaRepositoryAdapter<AdditionalSkillProgress, AdditionalSkillProgressEntity>
    implements AdditionalSkillProgressRepository {
  private final AdditionalSkillProgressJpaRepository jpaRepository;
  private final AdditionalSkillRepository additionalSkillRepository;

  public AdditionalSkillDatabaseProgressRepository(
      AdditionalSkillProgressJpaRepository jpaRepository,
      AdditionalSkillRepository additionalSkillRepository) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillProgressMapper::fromDomain,
        AdditionalSkillProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
    this.additionalSkillRepository = additionalSkillRepository;
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
    Page<AdditionalSkillProgressEntity> entities =
        jpaRepository.findAll(
            AdditionalSkillProgressSpecification.hasStudent(UserMapper.fromDomain(student)),
            PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));

    List<AdditionalSkill> additionalSkills =
        additionalSkillRepository.findAllByIds(
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
    return new PagedResult<>(
        progresses,
        new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), entities.getTotalElements()));
  }
}
