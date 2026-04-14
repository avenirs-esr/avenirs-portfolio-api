package fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.output.repository.DeclaredSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.specification.DeclaredSkillProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class DeclaredSkillProgressDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<DeclaredSkillProgress, DeclaredSkillProgressEntity>
    implements DeclaredSkillProgressRepository {
  private final DeclaredSkillProgressJpaRepository jpaRepository;
  @PersistenceContext private EntityManager em;

  public DeclaredSkillProgressDatabaseRepository(DeclaredSkillProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        DeclaredSkillProgressEntity.class,
        DeclaredSkillProgressMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public boolean declaredSkillProgressAlreadyExists(DeclaredSkillProgress declaredSkillProgress) {
    return jpaRepository.exists(
        DeclaredSkillProgressSpecification.declaredSkillProgressAlreadyExists(
            declaredSkillProgress.getSkill(), declaredSkillProgress.getStudent().getId()));
  }

  @Override
  public List<DeclaredSkillProgress> findAllByStudent(Student student) {
    return findAll(hasStudent(student));
  }

  @Override
  public PagedResult<DeclaredSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria) {
    var specification = hasStudent(student);
    return findAll(
        specification,
        PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()),
        FetchGraph.init().fetch("student").fetch("declaredSkill"));
  }

  @Override
  public PagedResult<DeclaredSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword, SortCriteria sortCriteria) {
    var specification =
        hasStudent(student)
            .and(DeclaredSkillProgressSpecification.search(keyword))
            .and(DeclaredSkillProgressSpecification.withSort(sortCriteria));
    return findAllByStudent(specification, pageCriteria);
  }

  private PagedResult<DeclaredSkillProgress> findAllByStudent(
      Specification<DeclaredSkillProgressEntity> specification, PageCriteria pageCriteria) {
    return findAll(specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));
  }
}
