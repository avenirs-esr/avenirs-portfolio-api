package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.DeclaredSkillEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredSkillMapperTest {

  private final UUID id = UUID.randomUUID();
  private final String libelle = "Java Programming";
  private final EExternalSkillType type = EExternalSkillType.ROME4;
  private final List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");
  private final Instant createdAt = Instant.parse("2023-01-01T00:00:00Z");
  private final Instant updatedAt = Instant.parse("2023-12-31T23:59:59Z");

  @Test
  void shouldMapEntityToDomain() {
    BddLogger.given("an DeclaredSkillEntity");
    DeclaredSkillEntity entity =
        DeclaredSkillEntity.of(id, libelle, type, pathSegments, createdAt, updatedAt);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    DeclaredSkill domain = DeclaredSkillMapper.INSTANCE.toDomain(entity);

    BddLogger.then("it should map all fields correctly");
    assertNotNull(domain);
    assertEquals(id, domain.getId());
    assertEquals(libelle, domain.getLibelle());
    assertEquals(type, domain.getType());
    assertEquals(pathSegments, domain.getPathSegments());
    assertEquals(createdAt, domain.getCreatedAt());
    assertEquals(updatedAt, domain.getUpdatedAt());
  }

  @Test
  void shouldMapDomainToEntity() {
    BddLogger.given("an DeclaredSkill domain model");
    DeclaredSkill domain =
        DeclaredSkill.toDomain(id, libelle, type, pathSegments, createdAt, updatedAt);

    BddLogger.when("mapping to entity");
    DeclaredSkillEntity entity = DeclaredSkillMapper.INSTANCE.fromDomain(domain);

    BddLogger.then("it should map all fields correctly");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
    assertEquals(pathSegments, entity.getPathSegments());
  }

  @Test
  void shouldMapEntityToDomainWithNullPathSegments() {
    BddLogger.given("an entity with null pathSegments");
    DeclaredSkillEntity entity =
        DeclaredSkillEntity.of(id, libelle, type, null, createdAt, updatedAt);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    DeclaredSkill domain = DeclaredSkillMapper.INSTANCE.toDomain(entity);

    BddLogger.then("it should map with empty pathSegments list");
    assertNotNull(domain);
    assertNotNull(domain.getPathSegments());
    assertTrue(domain.getPathSegments().isEmpty());
    assertEquals(id, domain.getId());
    assertEquals(libelle, domain.getLibelle());
    assertEquals(type, domain.getType());
  }

  @Test
  void shouldMapDomainToEntityWithNullPathSegments() {
    BddLogger.given("a domain model with null pathSegments");
    DeclaredSkill domain = DeclaredSkill.toDomain(id, libelle, type, null, createdAt, updatedAt);

    BddLogger.when("mapping to entity");
    DeclaredSkillEntity entity = DeclaredSkillMapper.INSTANCE.fromDomain(domain);

    BddLogger.then("it should map with empty pathSegments list");
    assertNotNull(entity);
    assertNotNull(entity.getPathSegments());
    assertTrue(entity.getPathSegments().isEmpty());
    assertEquals(id, entity.getId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
  }

  @Test
  void shouldMapEntityToDomainWithEmptyPathSegments() {
    BddLogger.given("an entity with empty pathSegments");
    List<String> emptyPathSegments = List.of();
    DeclaredSkillEntity entity =
        DeclaredSkillEntity.of(id, libelle, type, emptyPathSegments, createdAt, updatedAt);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    DeclaredSkill domain = DeclaredSkillMapper.INSTANCE.toDomain(entity);

    BddLogger.then("it should map with empty pathSegments");
    assertNotNull(domain);
    assertNotNull(domain.getPathSegments());
    assertTrue(domain.getPathSegments().isEmpty());
  }

  @Test
  void shouldPreserveAllFieldsInRoundTripMapping() {
    BddLogger.given("a complete domain model");
    DeclaredSkill originalDomain =
        DeclaredSkill.toDomain(id, libelle, type, pathSegments, createdAt, updatedAt);

    BddLogger.when("mapping to entity and back to domain");
    DeclaredSkillEntity entity = DeclaredSkillMapper.INSTANCE.fromDomain(originalDomain);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);
    DeclaredSkill resultDomain = DeclaredSkillMapper.INSTANCE.toDomain(entity);

    BddLogger.then("all fields should be preserved");
    assertNotNull(resultDomain);
    assertEquals(originalDomain.getId(), resultDomain.getId());
    assertEquals(originalDomain.getLibelle(), resultDomain.getLibelle());
    assertEquals(originalDomain.getType(), resultDomain.getType());
    assertEquals(originalDomain.getPathSegments(), resultDomain.getPathSegments());
    assertEquals(originalDomain.getCreatedAt(), resultDomain.getCreatedAt());
    assertEquals(originalDomain.getUpdatedAt(), resultDomain.getUpdatedAt());
  }

  @Test
  void shouldMapMultipleSegmentsCorrectly() {
    BddLogger.given("an entity with multiple path segments");
    List<String> multipleSegments = List.of("A", "B", "C", "D", "E");
    DeclaredSkillEntity entity =
        DeclaredSkillEntity.of(id, libelle, type, multipleSegments, createdAt, updatedAt);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    DeclaredSkill domain = DeclaredSkillMapper.INSTANCE.toDomain(entity);

    BddLogger.then("all segments should be preserved");
    assertNotNull(domain);
    assertEquals(multipleSegments.size(), domain.getPathSegments().size());
    assertEquals(multipleSegments, domain.getPathSegments());
  }

  @Test
  void shouldMapDifferentSkillTypes() {
    BddLogger.given("entities with different skill types");
    EExternalSkillType[] types = {
      EExternalSkillType.ROME4,
      EExternalSkillType.XXI,
      EExternalSkillType.CASOC,
      EExternalSkillType.CASOL
    };

    for (EExternalSkillType skillType : types) {
      BddLogger.when("mapping entity with type " + skillType);
      DeclaredSkillEntity entity =
          DeclaredSkillEntity.of(id, libelle, skillType, pathSegments, createdAt, updatedAt);
      entity.setCreatedAt(createdAt);
      entity.setUpdatedAt(updatedAt);

      DeclaredSkill domain = DeclaredSkillMapper.INSTANCE.toDomain(entity);

      BddLogger.then("the type should be correctly mapped");
      assertEquals(skillType, domain.getType());
    }
  }
}
