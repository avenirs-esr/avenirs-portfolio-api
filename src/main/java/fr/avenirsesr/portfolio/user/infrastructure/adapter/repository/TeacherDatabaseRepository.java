package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.TeacherRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.TeacherMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TeacherDatabaseRepository extends GenericJpaRepositoryAdapter<Teacher, TeacherEntity>
    implements TeacherRepository {
  private final TeacherJpaRepository jpaRepository;

  public TeacherDatabaseRepository(TeacherJpaRepository repository) {
    super(repository, repository, TeacherMapper::fromDomain, TeacherMapper::toDomain);
    this.jpaRepository = repository;
  }
}
