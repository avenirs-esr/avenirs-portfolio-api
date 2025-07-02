package fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.CohortFixture;
import fr.avenirsesr.portfolio.porgramprogress.infrastructure.fixture.ProgramProgressFixture;
import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.InstitutionTranslationEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramTranslationEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.HashSet;
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
    // Given
    ProgramProgress programProgress = ProgramProgressFixture.create().toModel();
    User user = UserFixture.createStudent().toModel();
    Set<User> users = new HashSet<>();
    users.add(user);

    Cohort cohort =
        CohortFixture.create()
            .withId(id)
            .withName(name)
            .withDescription(description)
            .withProgramProgress(programProgress)
            .withUsers(users)
            .withAmsSet(new HashSet<>())
            .toModel();

    // When
    CohortEntity entity = CohortMapper.fromDomain(cohort);

    // Then
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(name, entity.getName());
    assertEquals(description, entity.getDescription());
    assertNotNull(entity.getProgramProgress());
    assertEquals(programProgress.getId(), entity.getProgramProgress().getId());

    assertEquals(users.size(), entity.getUsers().size());
    for (UserEntity userEntity : entity.getUsers()) {
      assertTrue(users.stream().anyMatch(u -> u.getId().equals(userEntity.getId())));
    }

    assertTrue(entity.getAmsEntities().isEmpty());
  }

  @Test
  void shouldMapFromEntityToDomain() {
    // Given
    UserEntity studentEntity = new UserEntity();
    studentEntity.setId(UUID.randomUUID());
    studentEntity.setFirstName("John");
    studentEntity.setLastName("Doe");
    studentEntity.setEmail("john.doe@example.com");

    StudentEntity student = new StudentEntity();
    student.setActive(true);
    student.setBio("Student bio");
    student.setProfilePicture("profile.jpg");
    student.setCoverPicture("cover.jpg");
    studentEntity.setStudent(student);

    TeacherEntity teacher = new TeacherEntity();
    teacher.setActive(false);
    teacher.setBio("Teacher bio");
    teacher.setProfilePicture("teacher-profile.jpg");
    teacher.setCoverPicture("teacher-cover.jpg");
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

    ProgramProgressEntity programProgressEntity = new ProgramProgressEntity();
    programProgressEntity.setId(UUID.randomUUID());
    programProgressEntity.setProgram(programEntity);
    programProgressEntity.setStudent(studentEntity);
    programProgressEntity.setSkills(new HashSet<>());

    UserEntity userEntity = new UserEntity();
    userEntity.setId(UUID.randomUUID());
    userEntity.setFirstName("Jane");
    userEntity.setLastName("Smith");
    userEntity.setEmail("jane.smith@example.com");

    StudentEntity userStudent = new StudentEntity();
    userStudent.setActive(true);
    userStudent.setBio("User student bio");
    userStudent.setProfilePicture("user-profile.jpg");
    userStudent.setCoverPicture("user-cover.jpg");
    userEntity.setStudent(userStudent);

    TeacherEntity userTeacher = new TeacherEntity();
    userTeacher.setActive(false);
    userTeacher.setBio("User teacher bio");
    userTeacher.setProfilePicture("user-teacher-profile.jpg");
    userTeacher.setCoverPicture("user-teacher-cover.jpg");
    userEntity.setTeacher(userTeacher);

    Set<UserEntity> userEntities = new HashSet<>();
    userEntities.add(userEntity);

    CohortEntity entity = new CohortEntity();
    entity.setId(id);
    entity.setName(name);
    entity.setDescription(description);
    entity.setProgramProgress(programProgressEntity);
    entity.setUsers(userEntities);
    entity.setAmsEntities(new HashSet<>());

    // When
    Cohort mappedCohort = CohortMapper.toDomain(entity);

    // Then
    assertNotNull(mappedCohort);
    assertEquals(id, mappedCohort.getId());
    assertEquals(name, mappedCohort.getName());
    assertEquals(description, mappedCohort.getDescription());
    assertNotNull(mappedCohort.getProgramProgress());
    assertEquals(programProgressEntity.getId(), mappedCohort.getProgramProgress().getId());
    assertEquals(userEntities.size(), mappedCohort.getUsers().size());
    assertTrue(mappedCohort.getAmsSet().isEmpty());
  }

  @Test
  void shouldMapWithEmptyCollections() {
    // Given
    UserEntity studentEntity = new UserEntity();
    studentEntity.setId(UUID.randomUUID());
    studentEntity.setFirstName("John");
    studentEntity.setLastName("Doe");
    studentEntity.setEmail("john.doe@example.com");

    StudentEntity student = new StudentEntity();
    student.setActive(true);
    student.setBio("Student bio");
    student.setProfilePicture("profile.jpg");
    student.setCoverPicture("cover.jpg");
    studentEntity.setStudent(student);

    TeacherEntity teacher = new TeacherEntity();
    teacher.setActive(false);
    teacher.setBio("Teacher bio");
    teacher.setProfilePicture("teacher-profile.jpg");
    teacher.setCoverPicture("teacher-cover.jpg");
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

    ProgramProgressEntity programProgressEntity = new ProgramProgressEntity();
    programProgressEntity.setId(UUID.randomUUID());
    programProgressEntity.setProgram(programEntity);
    programProgressEntity.setStudent(studentEntity);
    programProgressEntity.setSkills(new HashSet<>());

    CohortEntity entity = new CohortEntity();
    entity.setId(id);
    entity.setName(name);
    entity.setDescription(description);
    entity.setProgramProgress(programProgressEntity);
    entity.setUsers(new HashSet<>());
    entity.setAmsEntities(new HashSet<>());

    // Then
    assertNotNull(entity);
    assertTrue(entity.getUsers().isEmpty());
    assertTrue(entity.getAmsEntities().isEmpty());

    // When
    Cohort mappedCohort = CohortMapper.toDomain(entity);

    // Then
    assertNotNull(mappedCohort);
    assertTrue(mappedCohort.getUsers().isEmpty());
    assertTrue(mappedCohort.getAmsSet().isEmpty());
  }
}
