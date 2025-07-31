package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserDatabaseRepository extends GenericJpaRepositoryAdapter<User, UserEntity>
    implements UserRepository {
  public UserDatabaseRepository(UserJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, UserMapper::fromDomain, UserMapper::toDomain);
  }

  @Override
  public void save(Student student) {
    jpaRepository.save(UserMapper.fromDomain(student));
  }

  @Override
  public void saveAllStudents(List<Student> students) {
    jpaRepository.saveAll(students.stream().map(UserMapper::fromDomain).toList());
  }

  @Override
  public void save(Teacher teacher) {
    jpaRepository.save(UserMapper.fromDomain(teacher));
  }

  @Override
  public void saveAllTeachers(List<Teacher> teachers) {
    jpaRepository.saveAll(teachers.stream().map(UserMapper::fromDomain).toList());
  }

  @Override
  public long countAll() {
    return jpaRepository.count();
  }
}
