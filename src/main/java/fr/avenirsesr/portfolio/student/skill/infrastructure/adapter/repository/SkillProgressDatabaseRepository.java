package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.port.output.repository.SkillProgressRepository;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.mapper.SkillProgressMapper;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.SkillProgressEntity;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.specification.SkillProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class SkillProgressDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<SkillProgress, SkillProgressEntity>
    implements SkillProgressRepository {
  private final SkillProgressJpaRepository jpaRepository;
  @PersistenceContext private EntityManager em;

  public SkillProgressDatabaseRepository(SkillProgressJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, SkillProgressEntity.class, SkillProgressMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean skillProgressAlreadyExists(SkillProgress skillProgress) {
    return jpaRepository.exists(
        SkillProgressSpecification.skillProgressAlreadyExists(
            skillProgress.getSkill().getId(), skillProgress.getStudent().getId()));
  }

  @Override
  public List<SkillProgress> findAllByStudent(Student student) {
    return findAll(hasStudent(student));
  }

  @Override
  public PagedResult<SkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, Boolean isValorized, SortCriteria sortCriteria) {
    var specification =
        hasStudent(student).and(SkillProgressSpecification.isValorized(isValorized));
    return findAll(
        specification,
        PageRequest.of(
            pageCriteria.page(),
            pageCriteria.pageSize(),
            SkillProgressSpecification.toSort(sortCriteria)),
        FetchGraph.init().fetch("student").fetch("skill"));
  }

  @Override
  public PagedResult<SkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword, SortCriteria sortCriteria) {
    var specification = hasStudent(student).and(SkillProgressSpecification.search(keyword));
    return findAllByStudent(specification, pageCriteria, sortCriteria);
  }

  private PagedResult<SkillProgress> findAllByStudent(
      Specification<SkillProgressEntity> specification,
      PageCriteria pageCriteria,
      SortCriteria sortCriteria) {
    return findAll(
        specification,
        PageRequest.of(
            pageCriteria.page(),
            pageCriteria.pageSize(),
            SkillProgressSpecification.toSort(sortCriteria)));
  }
}
