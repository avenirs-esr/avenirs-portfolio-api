package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.StudentCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeStudent;
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
public class StudentSeeder {
  private static final String PATH_FILE = "seeder/students.json";
  private final FileReader fileReader;
  private final StudentService studentService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<StudentEntity> seed(List<UserEntity> savedUsers) {
    return seed(savedUsers, List.of());
  }

  @Transactional
  public List<StudentEntity> seed(
      List<UserEntity> savedUsers, List<SelfKnowledgeCategoryEntity> mandatoryCategories) {
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    log.info("Seeding Students...");

    List<StudentCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<StudentCreationData>>() {});
          case FAKER ->
              IntStream.range(0, SeederConfig.USERS_NB_OF_STUDENT)
                  .mapToObj(i -> savedUsers.get(new Random().nextInt(savedUsers.size())))
                  .map(FakeStudent::create)
                  .map(FakeStudent::toEntity)
                  .map(
                      fakeStudent ->
                          new StudentCreationData(
                              fakeStudent.getUser().getId(),
                              fakeStudent.getBio(),
                              fakeStudent.getInstitutionEmail()))
                  .toList();
        };

    List<Student> students = new ArrayList<>();
    creationData.forEach(
        data -> {
          var student =
              studentService.createStudent(data.userId(), data.institutionEmail(), data.bio());
          students.add(student);
        });

    log.info("✔ {} students synced", students.size());

    return students.stream().map(StudentMapper.INSTANCE::fromDomain).toList();
  }
}
