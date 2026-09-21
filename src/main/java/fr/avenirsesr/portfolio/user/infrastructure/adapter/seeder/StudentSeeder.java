package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.seeder.domain.model.enums.ESeedMode;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.output.client.ExternalUserClient;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.StudentCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data.UserPrincipalCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake.FakeStudent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private static final String TABLE_NAME = "student-institutions-and-groups";
  private static final String PATH_FILE = "seeder/students.json";
  private static final String USER_PRINCIPAL_PATH_FILE = "seeder/user-principal.json";
  private final FileReader fileReader;
  private final StudentService studentService;
  private final ExternalUserClient externalUserClient;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  @Transactional
  public List<StudentEntity> seed(List<UserEntity> savedUsers) {
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

    Map<UUID, String> eppnByUserId = loadEppnByUserId();

    List<Student> students = new ArrayList<>();
    creationData.forEach(
        data -> {
          Affiliations affiliations = fetchAffiliations(data.userId(), eppnByUserId);
          var student =
              studentService.createStudent(
                  data.userId(),
                  data.institutionEmail(),
                  affiliations.institutionIds(),
                  affiliations.groupIds(),
                  data.bio());
          students.add(student);
        });

    log.info("✔ {} students synced", students.size());

    return students.stream().map(StudentMapper.INSTANCE::fromDomain).toList();
  }

  public String tableName() {
    return TABLE_NAME;
  }

  @Transactional
  public int seedAlone(ESeedMode mode) {
    log.info("Seeding {} with mode {}...", TABLE_NAME, mode);

    List<StudentCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<StudentCreationData>>() {});
          case FAKER -> List.of();
        };

    Map<UUID, String> eppnByUserId = loadEppnByUserId();

    int processed = 0;
    for (StudentCreationData data : creationData) {
      if (!studentService.existsById(data.userId())) {
        log.warn("Skipping student affiliations seed: no student found for {}", data.userId());
        continue;
      }

      if (mode == ESeedMode.INSERT_ONLY) {
        var student = studentService.getStudentById(data.userId());
        boolean hasAffiliations =
            !student.getInstitutionIds().isEmpty() || !student.getGroupIds().isEmpty();
        if (hasAffiliations) {
          continue;
        }
      }

      Affiliations affiliations = fetchAffiliations(data.userId(), eppnByUserId);
      studentService.updateAffiliations(
          data.userId(), affiliations.institutionIds(), affiliations.groupIds());
      processed++;
    }

    log.info("✔ {} {} rows processed", processed, TABLE_NAME);
    return processed;
  }

  private Map<UUID, String> loadEppnByUserId() {
    return fileReader
        .readJSON(USER_PRINCIPAL_PATH_FILE, new TypeReference<List<UserPrincipalCreationData>>() {})
        .stream()
        .collect(
            Collectors.toMap(UserPrincipalCreationData::userId, UserPrincipalCreationData::eppn));
  }

  private Affiliations fetchAffiliations(UUID userId, Map<UUID, String> eppnByUserId) {
    String eppn = eppnByUserId.get(userId);
    if (eppn == null || eppn.isBlank()) {
      log.warn("No eppn found for student {}, back-office affiliations skipped", userId);
      return new Affiliations(List.of(), List.of());
    }

    return externalUserClient
        .getByEppn(eppn)
        .map(
            externalUser ->
                new Affiliations(
                    externalUser.institutionIds() == null
                        ? List.of()
                        : externalUser.institutionIds(),
                    externalUser.groupIds() == null ? List.of() : externalUser.groupIds()))
        .orElseGet(
            () -> {
              log.warn("Back-office has no external user for eppn {}", eppn);
              return new Affiliations(List.of(), List.of());
            });
  }

  private record Affiliations(List<UUID> institutionIds, List<UUID> groupIds) {}
}
