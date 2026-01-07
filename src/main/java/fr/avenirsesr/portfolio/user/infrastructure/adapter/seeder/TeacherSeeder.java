package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.port.input.TeacherService;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.TeacherMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.TeacherCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeTeacher;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeacherSeeder {
  private static final String PATH_FILE = "seeder/teachers.json";
  private final FileReader fileReader;
  private final TeacherService teacherService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<TeacherEntity> seed(List<UserEntity> savedUsers) {
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    log.info("Seeding Teachers...");

    List<TeacherCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<TeacherCreationData>>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.USERS_NB_OF_TEACHER)
                  .mapToObj(i -> savedUsers.get(new Random().nextInt(savedUsers.size())))
                  .map(FakeTeacher::create)
                  .map(FakeTeacher::toEntity)
                  .map(
                      fakeTeacher ->
                          new TeacherCreationData(
                              fakeTeacher.getUser().getId(), fakeTeacher.getBio()))
                  .toList();
        };

    List<Teacher> teachers = new ArrayList<>();
    creationData.forEach(
        data -> {
          var teacher = teacherService.createTeacher(data.userId(), data.bio());
          teachers.add(teacher);
        });

    log.info("✔ {} teachers synced", teachers.size());

    return teachers.stream().map(TeacherMapper.INSTANCE::fromDomain).toList();
  }
}
