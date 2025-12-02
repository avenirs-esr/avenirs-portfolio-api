package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification.AdditionalSkillProgressSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentOwnershipSpecification;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class AdditionalSkillProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<AdditionalSkillProgress, AdditionalSkillProgressEntity>
    implements AdditionalSkillProgressRepository {
  private final AdditionalSkillProgressJpaRepository jpaRepository;

  public AdditionalSkillProgressDatabaseRepository(
      AdditionalSkillProgressJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillProgressMapper::fromDomain,
        AdditionalSkillProgressMapper::toDomain);
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
    return findAll(StudentOwnershipSpecification.hasStudent(student));
  }

  @Override
  public PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria) {
    var specification =
        StudentOwnershipSpecification.<AdditionalSkillProgressEntity>hasStudent(student);
    return findAllByStudent(specification, pageCriteria);
  }

  @Override
  public PagedResult<AdditionalSkillProgress> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword) {
    var specification =
        StudentOwnershipSpecification.<AdditionalSkillProgressEntity>hasStudent(student)
            .and(AdditionalSkillProgressSpecification.search(keyword));
    return findAllByStudent(specification, pageCriteria);
  }

  private PagedResult<AdditionalSkillProgress> findAllByStudent(
      Specification<AdditionalSkillProgressEntity> specification, PageCriteria pageCriteria) {
    return findAll(specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));
  }
}
