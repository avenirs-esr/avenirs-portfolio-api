package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.output.repository.DeclaredExperienceRepository;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.specification.DeclaredExperienceSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class DeclaredExperienceDatabaseRepository
    extends GenericUserJpaRepositoryAdapter<DeclaredExperience, DeclaredExperienceEntity>
    implements DeclaredExperienceRepository {

  public DeclaredExperienceDatabaseRepository(DeclaredExperienceJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        DeclaredExperienceEntity.class,
        DeclaredExperienceMapper.INSTANCE);
  }

  @Override
  public PagedResult<DeclaredExperience> findAllByStudent(
      Student student, PageCriteria pageCriteria) {
    return findAll(
        hasStudent(student).and(DeclaredExperienceSpecification.ordered()),
        PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));
  }

  @Override
  public PagedResult<DeclaredExperience> findAllByStudent(
      Student student, PageCriteria pageCriteria, String keyword) {
    var specification =
        hasStudent(student)
            .and(DeclaredExperienceSpecification.ordered())
            .and(DeclaredExperienceSpecification.search(keyword));

    return findAll(specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize()));
  }
}
