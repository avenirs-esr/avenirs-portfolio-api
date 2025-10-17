package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class StudentDatabaseRepository extends GenericJpaRepositoryAdapter<Student, StudentEntity>
    implements StudentRepository {
  private final StudentJpaRepository jpaRepository;

  public StudentDatabaseRepository(StudentJpaRepository repository) {
    super(repository, repository, StudentMapper::fromDomain, StudentMapper::toDomain);
    this.jpaRepository = repository;
  }

  public void saveAllEntities(List<StudentEntity> entities) {
    jpaRepository.saveAll(entities);
  }
}
