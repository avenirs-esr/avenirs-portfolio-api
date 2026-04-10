package fr.avenirsesr.portfolio.declaredskill.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeclaredSkillTest {

  private final UUID id = UUID.randomUUID();
  private final String libelle = "Java Programming";
  private final EExternalSkillType type = EExternalSkillType.ROME4;
  private final List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");

  @Test
  void shouldCreateDeclaredSkillWithAllFields() {
    BddLogger.given("valid declared skill data");

    BddLogger.when("creating a new DeclaredSkill");
    DeclaredSkill skill = DeclaredSkill.create(id, libelle, type, pathSegments);

    BddLogger.then("it should create an instance with all fields set");
    assertNotNull(skill);
    assertNotNull(skill.getId());
    assertEquals(id, skill.getId());
    assertEquals(libelle, skill.getLibelle());
    assertEquals(type, skill.getType());
    assertEquals(pathSegments, skill.getPathSegments());
    assertNotNull(skill.getCreatedAt());
    assertNotNull(skill.getUpdatedAt());
  }

  @Test
  void shouldGenerateIdAutomatically() {
    BddLogger.given("valid declared skill data");

    BddLogger.when("creating a new DeclaredSkill");
    DeclaredSkill skill = DeclaredSkill.create(id, libelle, type, pathSegments);

    BddLogger.then("it should generate an ID automatically");
    assertNotNull(skill.getId());
    assertNotEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), skill.getId());
  }

  @Test
  void shouldSetTimestampsOnCreation() {
    BddLogger.given("valid declared skill data");
    Instant beforeCreation = Instant.now();

    BddLogger.when("creating a new DeclaredSkill");
    DeclaredSkill skill = DeclaredSkill.create(id, libelle, type, pathSegments);

    BddLogger.then("it should set createdAt and updatedAt to current time");
    assertNotNull(skill.getCreatedAt());
    assertNotNull(skill.getUpdatedAt());
    assertTrue(
        skill.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)), "createdAt should be recent");
    assertTrue(
        skill.getUpdatedAt().isAfter(beforeCreation.minusSeconds(1)), "updatedAt should be recent");
    assertEquals(skill.getCreatedAt(), skill.getUpdatedAt());
  }

  @Test
  void shouldCreateFromDomainWithAllFields() {
    BddLogger.given("complete domain data");
    Instant createdAt = Instant.now().minusSeconds(3600);
    Instant updatedAt = Instant.now();

    BddLogger.when("creating DeclaredSkill from domain data");
    DeclaredSkill skill =
        DeclaredSkill.toDomain(id, libelle, type, pathSegments, createdAt, updatedAt);

    BddLogger.then("it should create an instance with all provided fields");
    assertNotNull(skill);
    assertEquals(id, skill.getId());
    assertEquals(libelle, skill.getLibelle());
    assertEquals(type, skill.getType());
    assertEquals(pathSegments, skill.getPathSegments());
    assertEquals(createdAt, skill.getCreatedAt());
    assertEquals(updatedAt, skill.getUpdatedAt());
  }

  @Test
  void shouldCreateWithNullPathSegments() {
    BddLogger.given("declared skill data with null pathSegments");

    BddLogger.when("creating a new DeclaredSkill");
    DeclaredSkill skill = DeclaredSkill.create(id, libelle, type, null);

    BddLogger.then("it should convert null pathSegments to empty list");
    assertNotNull(skill);
    assertNotNull(skill.getPathSegments());
    assertTrue(skill.getPathSegments().isEmpty());
    assertEquals(id, skill.getId());
    assertEquals(libelle, skill.getLibelle());
    assertEquals(type, skill.getType());
  }

  @Test
  void shouldCreateWithEmptyPathSegments() {
    BddLogger.given("declared skill data with empty pathSegments");
    List<String> emptyPathSegments = List.of();

    BddLogger.when("creating a new DeclaredSkill");
    DeclaredSkill skill = DeclaredSkill.create(id, libelle, type, emptyPathSegments);

    BddLogger.then("it should accept empty pathSegments");
    assertNotNull(skill);
    assertNotNull(skill.getPathSegments());
    assertTrue(skill.getPathSegments().isEmpty());
  }

  @Test
  void shouldAllowSettersForAllFields() {
    BddLogger.given("an DeclaredSkill instance");
    DeclaredSkill skill = DeclaredSkill.create(id, libelle, type, pathSegments);

    BddLogger.when("using setters to modify fields");
    String newLibelle = "New Skill";
    EExternalSkillType newType = EExternalSkillType.XXI;
    List<String> newPathSegments = List.of("A", "B", "C");

    skill.setLibelle(newLibelle);
    skill.setType(newType);
    skill.setPathSegments(newPathSegments);

    BddLogger.then("all fields should be updated");
    assertEquals(newLibelle, skill.getLibelle());
    assertEquals(newType, skill.getType());
    assertEquals(newPathSegments, skill.getPathSegments());
  }

  @Test
  void shouldPreserveImmutablePathSegmentsList() {
    BddLogger.given("an DeclaredSkill with pathSegments");
    List<String> originalPathSegments = List.of("A", "B", "C");
    DeclaredSkill skill = DeclaredSkill.create(id, libelle, type, originalPathSegments);

    BddLogger.when("getting pathSegments");
    List<String> retrievedPathSegments = skill.getPathSegments();

    BddLogger.then("it should return the same list reference");
    assertEquals(originalPathSegments, retrievedPathSegments);
    assertEquals(3, retrievedPathSegments.size());
  }
}
