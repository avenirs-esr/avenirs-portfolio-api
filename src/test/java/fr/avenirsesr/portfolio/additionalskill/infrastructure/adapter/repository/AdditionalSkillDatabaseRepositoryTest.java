package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdditionalSkillDatabaseRepositoryTest {

  @Autowired private AdditionalSkillDatabaseRepository repository;

  @Autowired private AdditionalSkillJpaRepository jpaRepository;

  @Test
  void shouldSaveAdditionalSkill() {
    BddLogger.given("a new AdditionalSkill");
    UUID externalSkillId = UUID.randomUUID();
    String libelle = "Java Programming";
    EAdditionalSkillType type = EAdditionalSkillType.ROME4;
    List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");

    AdditionalSkill skill = AdditionalSkill.create(externalSkillId, libelle, type, pathSegments);

    BddLogger.when("saving the skill");
    AdditionalSkill savedSkill = repository.save(skill);

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
    BddLogger.given("an AdditionalSkill with path segments");
    UUID externalSkillId = UUID.randomUUID();
    List<String> pathSegments = List.of("A", "B", "C", "D");

    AdditionalSkill skill =
        AdditionalSkill.create(
            externalSkillId, "Test Skill", EAdditionalSkillType.XXI, pathSegments);

    BddLogger.when("saving and retrieving the skill");
    AdditionalSkill savedSkill = repository.save(skill);
    Optional<AdditionalSkill> foundSkill = repository.findById(savedSkill.getId());

    BddLogger.then("path segments should be persisted and retrieved correctly");
    assertTrue(foundSkill.isPresent());
    assertEquals(pathSegments.size(), foundSkill.get().getPathSegments().size());
    assertEquals(pathSegments, foundSkill.get().getPathSegments());
  }

  @Test
  void shouldFindById() {
    BddLogger.given("a saved AdditionalSkill");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.create(
            UUID.randomUUID(),
            "Test Skill",
            EAdditionalSkillType.ROME4,
            List.of("Domain", "Issue"));
    AdditionalSkillEntity savedEntity = jpaRepository.save(entity);

    BddLogger.when("finding by ID");
    Optional<AdditionalSkill> result = repository.findById(savedEntity.getId());

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
    Optional<AdditionalSkill> result = repository.findById(nonExistentId);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldFindByExternalSkillId() {
    BddLogger.given("a saved AdditionalSkill");
    UUID externalSkillId = UUID.randomUUID();
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.create(
            externalSkillId, "Test Skill", EAdditionalSkillType.CASOC, List.of("A", "B"));
    jpaRepository.save(entity);

    BddLogger.when("finding by external skill ID");
    Optional<AdditionalSkill> result = repository.findByExternalSkillId(externalSkillId);

    BddLogger.then("it should return the skill");
    assertTrue(result.isPresent());
    assertEquals(externalSkillId, result.get().getExternalSkillId());
  }

  @Test
  void shouldReturnEmptyWhenExternalSkillIdNotFound() {
    BddLogger.given("a non-existent external skill ID");
    UUID nonExistentExternalId = UUID.randomUUID();

    BddLogger.when("finding by external skill ID");
    Optional<AdditionalSkill> result = repository.findByExternalSkillId(nonExistentExternalId);

    BddLogger.then("it should return empty");
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldUpdateAdditionalSkill() {
    BddLogger.given("a saved AdditionalSkill");
    UUID externalSkillId = UUID.randomUUID();
    AdditionalSkill skill =
        AdditionalSkill.create(
            externalSkillId, "Original Skill", EAdditionalSkillType.ROME4, List.of("A", "B"));
    AdditionalSkill savedSkill = repository.save(skill);

    BddLogger.when("updating the skill");
    savedSkill.setLibelle("Updated Skill");
    savedSkill.setPathSegments(List.of("X", "Y", "Z"));
    AdditionalSkill updatedSkill = repository.save(savedSkill);

    BddLogger.then("it should persist the changes");
    assertEquals(savedSkill.getId(), updatedSkill.getId());
    assertEquals("Updated Skill", updatedSkill.getLibelle());
    assertEquals(List.of("X", "Y", "Z"), updatedSkill.getPathSegments());

    Optional<AdditionalSkill> foundSkill = repository.findById(savedSkill.getId());
    assertTrue(foundSkill.isPresent());
    assertEquals("Updated Skill", foundSkill.get().getLibelle());
    assertEquals(List.of("X", "Y", "Z"), foundSkill.get().getPathSegments());
  }

  @Test
  void shouldUpdatePathSegments() {
    BddLogger.given("a saved AdditionalSkill with path segments");
    AdditionalSkill skill =
        AdditionalSkill.create(
            UUID.randomUUID(), "Test Skill", EAdditionalSkillType.XXI, List.of("A", "B"));
    AdditionalSkill savedSkill = repository.save(skill);

    BddLogger.when("updating path segments");
    List<String> newPathSegments = List.of("New", "Path", "Segments");
    savedSkill.setPathSegments(newPathSegments);
    repository.save(savedSkill);

    BddLogger.then("the path segments should be updated");
    Optional<AdditionalSkill> foundSkill = repository.findById(savedSkill.getId());
    assertTrue(foundSkill.isPresent());
    assertEquals(newPathSegments, foundSkill.get().getPathSegments());
  }

  @Test
  void shouldDeleteAdditionalSkill() {
    BddLogger.given("a saved AdditionalSkill");
    AdditionalSkill skill =
        AdditionalSkill.create(
            UUID.randomUUID(), "Test Skill", EAdditionalSkillType.CASOL, List.of("A"));
    AdditionalSkill savedSkill = repository.save(skill);
    UUID skillId = savedSkill.getId();

    BddLogger.when("deleting the skill");
    repository.removeFromDatabase(savedSkill);

    BddLogger.then("it should be removed from the database");
    Optional<AdditionalSkill> result = repository.findById(skillId);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldFindAllAdditionalSkills() {
    BddLogger.given("multiple saved AdditionalSkill");
    AdditionalSkill skill1 =
        AdditionalSkill.create(
            UUID.randomUUID(), "Skill 1", EAdditionalSkillType.ROME4, List.of("A"));
    AdditionalSkill skill2 =
        AdditionalSkill.create(
            UUID.randomUUID(), "Skill 2", EAdditionalSkillType.XXI, List.of("B"));
    AdditionalSkill skill3 =
        AdditionalSkill.create(
            UUID.randomUUID(), "Skill 3", EAdditionalSkillType.CASOC, List.of("C"));

    repository.save(skill1);
    repository.save(skill2);
    repository.save(skill3);

    BddLogger.when("finding all skills");
    List<AdditionalSkill> allSkills = repository.findAll();

    BddLogger.then("it should return all saved skills");
    assertTrue(allSkills.size() >= 3);
  }

  @Test
  void shouldHandleNullPathSegments() {
    BddLogger.given("an AdditionalSkill with null path segments");
    AdditionalSkill skill =
        AdditionalSkill.create(UUID.randomUUID(), "Test Skill", EAdditionalSkillType.ROME4, null);

    BddLogger.when("saving and retrieving the skill");
    AdditionalSkill savedSkill = repository.save(skill);
    Optional<AdditionalSkill> foundSkill = repository.findById(savedSkill.getId());

    BddLogger.then("it should convert null to empty list");
    assertTrue(foundSkill.isPresent());
    assertNotNull(foundSkill.get().getPathSegments());
    assertTrue(foundSkill.get().getPathSegments().isEmpty());
  }

  @Test
  void shouldHandleEmptyPathSegments() {
    BddLogger.given("an AdditionalSkill with empty path segments");
    List<String> emptySegments = List.of();
    AdditionalSkill skill =
        AdditionalSkill.create(
            UUID.randomUUID(), "Test Skill", EAdditionalSkillType.XXI, emptySegments);

    BddLogger.when("saving and retrieving the skill");
    AdditionalSkill savedSkill = repository.save(skill);
    Optional<AdditionalSkill> foundSkill = repository.findById(savedSkill.getId());

    BddLogger.then("it should handle empty path segments correctly");
    assertTrue(foundSkill.isPresent());
    assertNotNull(foundSkill.get().getPathSegments());
    assertTrue(foundSkill.get().getPathSegments().isEmpty());
  }
}
