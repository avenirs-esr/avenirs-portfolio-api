package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.specification.AdditionalSkillProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionalSkillProgressDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<AdditionalSkillProgress, AdditionalSkillProgressEntity>
    implements AdditionalSkillProgressRepository {
  private final AdditionalSkillProgressJpaRepository jpaRepository;
  @PersistenceContext private EntityManager em;

  public AdditionalSkillProgressDatabaseRepository(
      AdditionalSkillProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillProgressEntity.class,
        AdditionalSkillProgressMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
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
    return findAll(hasStudent(student));
  }

  @Override
  public PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria) {
    var specification = hasStudent(student);
    return findAll(
        specification,
        PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()),
        FetchGraph.init().fetch("student").fetch("additionalSkill"));
  }

  @Override
  public PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword) {
    var specification =
        hasStudent(student).and(AdditionalSkillProgressSpecification.search(keyword));
    return findAllByStudent(specification, pageCriteria);
  }

  private PagedResult<AdditionalSkillProgress> findAllByStudent(
      Specification<AdditionalSkillProgressEntity> specification, PageCriteria pageCriteria) {
    return findAll(specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));
  }
}
