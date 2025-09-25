package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionalSkillDatabaseProgressRepository
    extends GenericJpaRepositoryAdapter<AdditionalSkillProgress, AdditionalSkillProgressEntity>
    implements AdditionalSkillProgressRepository {
  private final AdditionalSkillProgressJpaRepository jpaRepository;

  public AdditionalSkillDatabaseProgressRepository(
      AdditionalSkillProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillProgressMapper::fromDomain,
        AdditionalSkillProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
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
            additionalSkillProgress.getSkill(), additionalSkillProgress.getStudent().getId()));
  }

  @Override
  public List<AdditionalSkillProgress> findAllByStudent(Student student) {
    return jpaRepository
        .findAll(AdditionalSkillProgressSpecification.hasStudent(UserMapper.fromDomain(student)))
        .stream()
        .map(AdditionalSkillProgressMapper::toDomain)
        .toList();
  }

  @Override
  public PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria) {
    var specification =
        AdditionalSkillProgressSpecification.hasStudent(UserMapper.fromDomain(student));
    return findAllByStudent(specification, pageCriteria);
  }

  @Override
  public PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword) {
    var specification =
        AdditionalSkillProgressSpecification.hasStudent(UserMapper.fromDomain(student))
            .and(AdditionalSkillProgressSpecification.search(keyword));
    return findAllByStudent(specification, pageCriteria);
  }

  private PagedResult<AdditionalSkillProgress> findAllByStudent(
      Specification<AdditionalSkillProgressEntity> specification, PageCriteria pageCriteria) {

    Page<AdditionalSkillProgressEntity> entities =
        jpaRepository.findAll(
            specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));

    List<AdditionalSkillProgress> progresses =
        entities.stream().map(AdditionalSkillProgressMapper::toDomain).toList();
    return new PagedResult<>(
        progresses,
        new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), entities.getTotalElements()));
  }
}
