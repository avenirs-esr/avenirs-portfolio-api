package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeStudent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentSeeder {
  private final StudentRepository studentRepository;

  @Transactional
  public List<StudentEntity> seed(List<UserEntity> savedUsers) {
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    log.info("Seeding Students ...");

    List<UserEntity> users = new ArrayList<>(savedUsers);

    Collections.shuffle(users, new Random("pick-students".hashCode()));

    List<UserEntity> usersToAdd =
        new ArrayList<>(users.subList(0, SeederConfig.USERS_NB_OF_STUDENT));

    List<StudentEntity> students = new ArrayList<>();
    for (UserEntity user : usersToAdd) {
      students.add(FakeStudent.create(user).toEntity());
    }

    studentRepository.saveAll(students.stream().map(StudentMapper::toDomain).toList());
    log.info("✔ {} students synced", students.size());

    return students;
  }
}
