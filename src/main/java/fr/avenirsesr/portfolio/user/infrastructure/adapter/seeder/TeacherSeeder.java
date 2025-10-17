package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.TeacherRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.TeacherMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeTeacher;
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
public class TeacherSeeder {
  private final TeacherRepository teacherRepository;

  @Transactional
  public List<TeacherEntity> seed(List<UserEntity> savedUsers) {
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    log.info("Seeding Teachers ...");

    List<UserEntity> users = new ArrayList<>(savedUsers);

    Collections.shuffle(users, new Random("pick-teachers".hashCode()));

    List<UserEntity> usersToAdd =
        new ArrayList<>(users.subList(0, SeederConfig.USERS_NB_OF_TEACHER));

    List<TeacherEntity> teachers = new ArrayList<>();
    for (UserEntity user : usersToAdd) {
      teachers.add(FakeTeacher.create(user).toEntity());
    }

    teacherRepository.saveAll(teachers.stream().map(TeacherMapper::toDomain).toList());
    log.info("✔ {} teachers synced", teachers.size());

    return teachers;
  }
}
