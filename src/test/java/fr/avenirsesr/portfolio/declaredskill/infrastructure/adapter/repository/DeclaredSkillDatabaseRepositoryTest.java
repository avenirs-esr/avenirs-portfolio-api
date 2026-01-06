package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.repository;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model.DeclaredSkillEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeclaredSkillDatabaseRepositoryTest extends ContainerConfigurationTest {

  @Autowired private DeclaredSkillDatabaseRepository repository;

  @Autowired private DeclaredSkillJpaRepository jpaRepository;

  @Test
  void shouldSaveDeclaredSkill() {
    BddLogger.given("a new DeclaredSkill");
    UUID externalSkillId = UUID.randomUUID();
    String libelle = "Java Programming";
    EExternalSkillType type = EExternalSkillType.ROME4;
    List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");

    DeclaredSkill skill = DeclaredSkill.create(externalSkillId, libelle, type, pathSegments);

    BddLogger.when("saving the skill");
    DeclaredSkill savedSkill = repository.save(skill);

    BddLogger.then("it should be persisted with all fields");
    assertNotNull(savedSkill);
    assertNotNull(savedSkill.getId());
    assertEquals(externalSkillId, savedSkill.getExternalSkillId());
    assertEquals(libelle, savedSkill.getLibelle());
    assertEquals(type, savedSkill.getType());
    assertEquals(pathSegments, savedSkill.getPathSegments());
  }

  @Test
  void shouldSaveWithPathSegments() {
    BddLogger.given("an DeclaredSkill with path segments");
    UUID externalSkillId = UUID.randomUUID();
    List<String> pathSegments = List.of("A", "B", "C", "D");

    DeclaredSkill skill =
        DeclaredSkill.create(externalSkillId, "Test Skill", EExternalSkillType.XXI, pathSegments);

    BddLogger.when("saving and retrieving the skill");
    DeclaredSkill savedSkill = repository.save(skill);
    Optional<DeclaredSkill> foundSkill = repository.findById(savedSkill.getId());

    BddLogger.then("path segments should be persisted and retrieved correctly");
    assertTrue(foundSkill.isPresent());
    assertEquals(pathSegments.size(), foundSkill.get().getPathSegments().size());
    assertEquals(pathSegments, foundSkill.get().getPathSegments());
  }

  @Test
  void shouldFindById() {
    BddLogger.given("a saved DeclaredSkill");
    DeclaredSkillEntity entity =
        DeclaredSkillEntity.create(
            UUID.randomUUID(), "Test Skill", EExternalSkillType.ROME4, List.of("Domain", "Issue"));
    DeclaredSkillEntity savedEntity = jpaRepository.save(entity);

    BddLogger.when("finding by ID");
    Optional<DeclaredSkill> result = repository.findById(savedEntity.getId());

    BddLogger.then("it should return the skill");
    assertTrue(result.isPresent());
    assertEquals(savedEntity.getId(), result.get().getId());
    assertEquals(savedEntity.getExternalSkillId(), result.get().getExternalSkillId());
    assertEquals(savedEntity.getLibelle(), result.get().getLibelle());
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    BddLogger.given("a non-existent ID");
    UUID nonExistentId = UUID.randomUUID();

    BddLogger.when("finding by ID");
    Optional<DeclaredSkill> result = repository.findById(nonExistentId);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldFindByExternalSkillId() {
    BddLogger.given("a saved DeclaredSkill");
    UUID externalSkillId = UUID.randomUUID();
    DeclaredSkillEntity entity =
        DeclaredSkillEntity.create(
            externalSkillId, "Test Skill", EExternalSkillType.CASOC, List.of("A", "B"));
    jpaRepository.save(entity);

    BddLogger.when("finding by external skill ID");
    Optional<DeclaredSkill> result = repository.findByExternalSkillId(externalSkillId);

    BddLogger.then("it should return the skill");
    assertTrue(result.isPresent());
    assertEquals(externalSkillId, result.get().getExternalSkillId());
  }

  @Test
  void shouldReturnEmptyWhenExternalSkillIdNotFound() {
    BddLogger.given("a non-existent external skill ID");
    UUID nonExistentExternalId = UUID.randomUUID();

    BddLogger.when("finding by external skill ID");
    Optional<DeclaredSkill> result = repository.findByExternalSkillId(nonExistentExternalId);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldUpdateDeclaredSkill() {
    BddLogger.given("a saved DeclaredSkill");
    UUID externalSkillId = UUID.randomUUID();
    DeclaredSkill skill =
        DeclaredSkill.create(
            externalSkillId, "Original Skill", EExternalSkillType.ROME4, List.of("A", "B"));
    DeclaredSkill savedSkill = repository.save(skill);

    BddLogger.when("updating the skill");
    savedSkill.setLibelle("Updated Skill");
    savedSkill.setPathSegments(List.of("X", "Y", "Z"));
    DeclaredSkill updatedSkill = repository.save(savedSkill);

    BddLogger.then("it should persist the changes");
    assertEquals(savedSkill.getId(), updatedSkill.getId());
    assertEquals("Updated Skill", updatedSkill.getLibelle());
    assertEquals(List.of("X", "Y", "Z"), updatedSkill.getPathSegments());

    Optional<DeclaredSkill> foundSkill = repository.findById(savedSkill.getId());
    assertTrue(foundSkill.isPresent());
    assertEquals("Updated Skill", foundSkill.get().getLibelle());
    assertEquals(List.of("X", "Y", "Z"), foundSkill.get().getPathSegments());
  }

  @Test
  void shouldUpdatePathSegments() {
    BddLogger.given("a saved DeclaredSkill with path segments");
    DeclaredSkill skill =
        DeclaredSkill.create(
            UUID.randomUUID(), "Test Skill", EExternalSkillType.XXI, List.of("A", "B"));
    DeclaredSkill savedSkill = repository.save(skill);

    BddLogger.when("updating path segments");
    List<String> newPathSegments = List.of("New", "Path", "Segments");
    savedSkill.setPathSegments(newPathSegments);
    repository.save(savedSkill);

    BddLogger.then("the path segments should be updated");
    Optional<DeclaredSkill> foundSkill = repository.findById(savedSkill.getId());
    assertTrue(foundSkill.isPresent());
    assertEquals(newPathSegments, foundSkill.get().getPathSegments());
  }

  @Test
  void shouldDeleteDeclaredSkill() {
    BddLogger.given("a saved DeclaredSkill");
    DeclaredSkill skill =
        DeclaredSkill.create(
            UUID.randomUUID(), "Test Skill", EExternalSkillType.CASOL, List.of("A"));
    DeclaredSkill savedSkill = repository.save(skill);
    UUID skillId = savedSkill.getId();

    BddLogger.when("deleting the skill");
    repository.removeFromDatabase(savedSkill);

    BddLogger.then("it should be removed from the database");
    Optional<DeclaredSkill> result = repository.findById(skillId);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldFindAllDeclaredSkills() {
    BddLogger.given("multiple saved DeclaredSkill");
    DeclaredSkill skill1 =
        DeclaredSkill.create(UUID.randomUUID(), "Skill 1", EExternalSkillType.ROME4, List.of("A"));
    DeclaredSkill skill2 =
        DeclaredSkill.create(UUID.randomUUID(), "Skill 2", EExternalSkillType.XXI, List.of("B"));
    DeclaredSkill skill3 =
        DeclaredSkill.create(UUID.randomUUID(), "Skill 3", EExternalSkillType.CASOC, List.of("C"));

    repository.save(skill1);
    repository.save(skill2);
    repository.save(skill3);

    BddLogger.when("finding all skills");
    List<DeclaredSkill> allSkills = repository.findAll();

    BddLogger.then("it should return all saved skills");
    assertTrue(allSkills.size() >= 3);
  }

  @Test
  void shouldHandleNullPathSegments() {
    BddLogger.given("an DeclaredSkill with null path segments");
    DeclaredSkill skill =
        DeclaredSkill.create(UUID.randomUUID(), "Test Skill", EExternalSkillType.ROME4, null);

    BddLogger.when("saving and retrieving the skill");
    DeclaredSkill savedSkill = repository.save(skill);
    Optional<DeclaredSkill> foundSkill = repository.findById(savedSkill.getId());

    BddLogger.then("it should convert null to empty list");
    assertTrue(foundSkill.isPresent());
    assertNotNull(foundSkill.get().getPathSegments());
    assertTrue(foundSkill.get().getPathSegments().isEmpty());
  }

  @Test
  void shouldHandleEmptyPathSegments() {
    BddLogger.given("an DeclaredSkill with empty path segments");
    List<String> emptySegments = List.of();
    DeclaredSkill skill =
        DeclaredSkill.create(
            UUID.randomUUID(), "Test Skill", EExternalSkillType.XXI, emptySegments);

    BddLogger.when("saving and retrieving the skill");
    DeclaredSkill savedSkill = repository.save(skill);
    Optional<DeclaredSkill> foundSkill = repository.findById(savedSkill.getId());

    BddLogger.then("it should handle empty path segments correctly");
    assertTrue(foundSkill.isPresent());
    assertNotNull(foundSkill.get().getPathSegments());
    assertTrue(foundSkill.get().getPathSegments().isEmpty());
  }

  @Test
  void shouldReturnExistingSkillWhenSaveOrGetOnDuplicateExternalSkillIdAndType() {
    BddLogger.given("an existing DeclaredSkillEntity with a given externalSkillId and type");
    UUID externalSkillId = UUID.randomUUID();
    EExternalSkillType type = EExternalSkillType.ROME4;

    DeclaredSkillEntity existingEntity =
        DeclaredSkillEntity.create(externalSkillId, "Existing Skill", type, List.of("A", "B"));
    existingEntity = jpaRepository.save(existingEntity);

    BddLogger.when(
        "calling saveOrGet with a domain object having the same externalSkillId and type");
    DeclaredSkill toSave =
        DeclaredSkill.create(externalSkillId, "New Name", type, List.of("X", "Y"));

    DeclaredSkill result = repository.saveOrGet(toSave);

    BddLogger.then("it should return a skill with the same externalSkillId and type");
    assertNotNull(result);
    assertEquals(existingEntity.getExternalSkillId(), result.getExternalSkillId());
    assertEquals(existingEntity.getType(), result.getType());
  }
}
