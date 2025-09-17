package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.CohortFixture;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.*;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.*;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CohortMapperTest {

  private final String name = "Test Cohort";
  private final String description = "Test Cohort Description";
  private final UUID id = UUID.randomUUID();
  private final ELanguage language = ELanguage.FRENCH;

  @Test
  void shouldMapFromDomainToEntity() {
    BddLogger.given("a Cohort mapper");
    TrainingPath trainingPath = TrainingPathFixture.create().toModel();
    User user = UserFixture.createStudent().toModel();
    Set<User> users = new HashSet<>();
    users.add(user);

    Cohort cohort =
        CohortFixture.create()
            .withId(id)
            .withName(name)
            .withDescription(description)
            .withTrainingPath(trainingPath)
            .withUsers(users)
            .withAmsSet(new HashSet<>())
            .toModel();

    BddLogger.when("mapping a domain Cohort to CohortEntity");
    CohortEntity entity = CohortMapper.fromDomain(cohort);

    BddLogger.when("it should return a correct CohortEntity");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(name, entity.getName());
    assertEquals(description, entity.getDescription());
    assertNotNull(entity.getTrainingPath());
    assertEquals(trainingPath.getId(), entity.getTrainingPath().getId());

    assertEquals(users.size(), entity.getUsers().size());
    for (UserEntity userEntity : entity.getUsers()) {
      assertTrue(users.stream().anyMatch(u -> u.getId().equals(userEntity.getId())));
    }

    assertTrue(entity.getAmsEntities().isEmpty());
  }

  @Test
  void shouldMapFromEntityToDomain() {
    BddLogger.given("a Cohort mapper");
    UserEntity studentEntity = new UserEntity();
    studentEntity.setId(UUID.randomUUID());
    studentEntity.setFirstName("John");
    studentEntity.setLastName("Doe");
    studentEntity.setEmail("john.doe@example.com");

    StudentEntity student = new StudentEntity();
    student.setActive(true);
    student.setBio("Student bio");
    studentEntity.setStudent(student);

    TeacherEntity teacher = TeacherEntity.of("Teacher bio", false);
    studentEntity.setTeacher(teacher);

    InstitutionEntity institutionEntity = new InstitutionEntity();
    institutionEntity.setId(UUID.randomUUID());
    institutionEntity.setEnabledFields(new HashSet<>());

    InstitutionTranslationEntity institutionTranslationEntity = new InstitutionTranslationEntity();
    institutionTranslationEntity.setLanguage(language);
    institutionTranslationEntity.setName("Institution Test");
    institutionTranslationEntity.setInstitution(institutionEntity);

    Set<InstitutionTranslationEntity> institutionTranslations = new HashSet<>();
    institutionTranslations.add(institutionTranslationEntity);
    institutionEntity.setTranslations(institutionTranslations);

    ProgramEntity programEntity = new ProgramEntity();
    programEntity.setId(UUID.randomUUID());
    programEntity.setAPC(true);
    programEntity.setInstitution(institutionEntity);

    ProgramTranslationEntity translationEntity = new ProgramTranslationEntity();
    translationEntity.setLanguage(language);
    translationEntity.setName("Programme Test");
    translationEntity.setProgram(programEntity);

    Set<ProgramTranslationEntity> translations = new HashSet<>();
    translations.add(translationEntity);
    programEntity.setTranslations(translations);

    TrainingPathEntity trainingPathEntity = new TrainingPathEntity();
    trainingPathEntity.setId(UUID.randomUUID());
    trainingPathEntity.setProgram(programEntity);
    trainingPathEntity.setSkillLevels(new HashSet<>());

    StudentProgressEntity studentProgressEntity = new StudentProgressEntity();
    studentProgressEntity.setId(UUID.randomUUID());
    studentProgressEntity.setStudent(studentEntity);
    studentProgressEntity.setTrainingPath(trainingPathEntity);
    studentProgressEntity.setSkillLevels(List.of(new SkillLevelProgressEntity()));

    UserEntity userEntity = new UserEntity();
    userEntity.setId(UUID.randomUUID());
    userEntity.setFirstName("Jane");
    userEntity.setLastName("Smith");
    userEntity.setEmail("jane.smith@example.com");

    StudentEntity userStudent = new StudentEntity();
    userStudent.setActive(true);
    userStudent.setBio("User student bio");
    userEntity.setStudent(userStudent);

    TeacherEntity userTeacher = TeacherEntity.of("User teacher bio", false);
    userEntity.setTeacher(userTeacher);

    Set<UserEntity> userEntities = new HashSet<>();
    userEntities.add(userEntity);

    CohortEntity entity = new CohortEntity();
    entity.setId(id);
    entity.setName(name);
    entity.setDescription(description);
    entity.setTrainingPath(trainingPathEntity);
    entity.setUsers(userEntities);
    entity.setAmsEntities(new HashSet<>());

    BddLogger.when("mapping a CohortEntity to domain Cohort");
    Cohort mappedCohort = CohortMapper.toDomain(entity);

    BddLogger.when("it should return a correct domain Cohort");
    assertNotNull(mappedCohort);
    assertEquals(id, mappedCohort.getId());
    assertEquals(name, mappedCohort.getName());
    assertEquals(description, mappedCohort.getDescription());
    assertNotNull(mappedCohort.getTrainingPath());
    assertEquals(trainingPathEntity.getId(), mappedCohort.getTrainingPath().getId());
    assertEquals(userEntities.size(), mappedCohort.getUsers().size());
    assertTrue(mappedCohort.getAmsSet().isEmpty());
  }

  @Test
  void shouldMapWithEmptyCollections() {
    BddLogger.given("a Cohort mapper");
    UserEntity studentEntity = new UserEntity();
    studentEntity.setId(UUID.randomUUID());
    studentEntity.setFirstName("John");
    studentEntity.setLastName("Doe");
    studentEntity.setEmail("john.doe@example.com");

    StudentEntity student = new StudentEntity();
    student.setActive(true);
    student.setBio("Student bio");
    studentEntity.setStudent(student);

    TeacherEntity teacher = TeacherEntity.of("Teacher bio", false);
    studentEntity.setTeacher(teacher);

    InstitutionEntity institutionEntity = new InstitutionEntity();
    institutionEntity.setId(UUID.randomUUID());
    institutionEntity.setEnabledFields(new HashSet<>());

    InstitutionTranslationEntity institutionTranslationEntity = new InstitutionTranslationEntity();
    institutionTranslationEntity.setLanguage(language);
    institutionTranslationEntity.setName("Institution Test");
    institutionTranslationEntity.setInstitution(institutionEntity);

    Set<InstitutionTranslationEntity> institutionTranslations = new HashSet<>();
    institutionTranslations.add(institutionTranslationEntity);
    institutionEntity.setTranslations(institutionTranslations);

    ProgramEntity programEntity = new ProgramEntity();
    programEntity.setId(UUID.randomUUID());
    programEntity.setAPC(true);
    programEntity.setInstitution(institutionEntity);

    ProgramTranslationEntity translationEntity = new ProgramTranslationEntity();
    translationEntity.setLanguage(language);
    translationEntity.setName("Programme Test");
    translationEntity.setProgram(programEntity);

    Set<ProgramTranslationEntity> translations = new HashSet<>();
    translations.add(translationEntity);
    programEntity.setTranslations(translations);

    TrainingPathEntity trainingPathEntity = new TrainingPathEntity();
    trainingPathEntity.setId(UUID.randomUUID());
    trainingPathEntity.setProgram(programEntity);
    trainingPathEntity.setSkillLevels(new HashSet<>());

    StudentProgressEntity studentProgressEntity = new StudentProgressEntity();
    studentProgressEntity.setId(UUID.randomUUID());
    studentProgressEntity.setStudent(studentEntity);
    studentProgressEntity.setTrainingPath(trainingPathEntity);
    studentProgressEntity.setSkillLevels(List.of(new SkillLevelProgressEntity()));

    CohortEntity entity = new CohortEntity();
    entity.setId(id);
    entity.setName(name);
    entity.setDescription(description);
    entity.setTrainingPath(trainingPathEntity);
    entity.setUsers(new HashSet<>());
    entity.setAmsEntities(new HashSet<>());

    assertNotNull(entity);
    assertTrue(entity.getUsers().isEmpty());
    assertTrue(entity.getAmsEntities().isEmpty());

    BddLogger.when("mapping an empty collection to domain Cohort");
    Cohort mappedCohort = CohortMapper.toDomain(entity);

    BddLogger.then("it should return a correct domain Cohort");
    assertNotNull(mappedCohort);
    assertTrue(mappedCohort.getUsers().isEmpty());
    assertTrue(mappedCohort.getAmsSet().isEmpty());
  }
}
