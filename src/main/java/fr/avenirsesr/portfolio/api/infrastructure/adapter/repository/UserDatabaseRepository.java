package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDatabaseRepository implements UserRepository {

  private final UserJpaRepository jpaRepository;

  @Override
  public void save(User user) {
    UserEntity userEntity = toEntity(user);
    jpaRepository.save(userEntity);
  }

  @Override
  public void saveAll(List<User> users) {
    List<UserEntity> usersEntities = users.stream().map(UserDatabaseRepository::toEntity).toList();
    jpaRepository.saveAll(usersEntities);
  }

  public static UserEntity toEntity(User user) {
    return new UserEntity(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getStudent() != null ? studentModelToEntity(user.getStudent()) : null,
        user.getTeacher() != null ? teacherModelToEntity(user.getTeacher()) : null);
  }

  private static StudentEntity studentModelToEntity(Student student) {
    return new StudentEntity(
        student.getBio(), student.getProfilePicture(), student.getCoverPicture());
  }

  private static TeacherEntity teacherModelToEntity(Teacher teacher) {
    return new TeacherEntity(
        teacher.getBio(), teacher.getProfilePicture(), teacher.getCoverPicture());
  }
}
