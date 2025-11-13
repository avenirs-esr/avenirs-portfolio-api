package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdditionalSkillMapperTest {

  private final UUID id = UUID.randomUUID();
  private final UUID externalSkillId = UUID.randomUUID();
  private final String libelle = "Java Programming";
  private final EAdditionalSkillType type = EAdditionalSkillType.ROME4;
  private final List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");
  private final Instant createdAt = Instant.parse("2023-01-01T00:00:00Z");
  private final Instant updatedAt = Instant.parse("2023-12-31T23:59:59Z");

  @Test
  void shouldMapEntityToDomain() {
    BddLogger.given("an AdditionalSkillEntity");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.of(id, externalSkillId, libelle, type, pathSegments);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    AdditionalSkill domain = AdditionalSkillMapper.toDomain(entity);

    BddLogger.then("it should map all fields correctly");
    assertNotNull(domain);
    assertEquals(id, domain.getId());
    assertEquals(externalSkillId, domain.getExternalSkillId());
    assertEquals(libelle, domain.getLibelle());
    assertEquals(type, domain.getType());
    assertEquals(pathSegments, domain.getPathSegments());
    assertEquals(createdAt, domain.getCreatedAt());
    assertEquals(updatedAt, domain.getUpdatedAt());
  }

  @Test
  void shouldMapDomainToEntity() {
    BddLogger.given("an AdditionalSkill domain model");
    AdditionalSkill domain =
        AdditionalSkill.toDomain(
            id, externalSkillId, libelle, type, pathSegments, createdAt, updatedAt);

    BddLogger.when("mapping to entity");
    AdditionalSkillEntity entity = AdditionalSkillMapper.fromDomain(domain);

    BddLogger.then("it should map all fields correctly");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(externalSkillId, entity.getExternalSkillId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
    assertEquals(pathSegments, entity.getPathSegments());
  }

  @Test
  void shouldMapEntityToDomainWithNullPathSegments() {
    BddLogger.given("an entity with null pathSegments");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.of(id, externalSkillId, libelle, type, null);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    AdditionalSkill domain = AdditionalSkillMapper.toDomain(entity);

    BddLogger.then("it should map with empty pathSegments list");
    assertNotNull(domain);
    assertNotNull(domain.getPathSegments());
    assertTrue(domain.getPathSegments().isEmpty());
    assertEquals(id, domain.getId());
    assertEquals(externalSkillId, domain.getExternalSkillId());
    assertEquals(libelle, domain.getLibelle());
    assertEquals(type, domain.getType());
  }

  @Test
  void shouldMapDomainToEntityWithNullPathSegments() {
    BddLogger.given("a domain model with null pathSegments");
    AdditionalSkill domain =
        AdditionalSkill.toDomain(id, externalSkillId, libelle, type, null, createdAt, updatedAt);

    BddLogger.when("mapping to entity");
    AdditionalSkillEntity entity = AdditionalSkillMapper.fromDomain(domain);

    BddLogger.then("it should map with empty pathSegments list");
    assertNotNull(entity);
    assertNotNull(entity.getPathSegments());
    assertTrue(entity.getPathSegments().isEmpty());
    assertEquals(id, entity.getId());
    assertEquals(externalSkillId, entity.getExternalSkillId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
  }

  @Test
  void shouldMapEntityToDomainWithEmptyPathSegments() {
    BddLogger.given("an entity with empty pathSegments");
    List<String> emptyPathSegments = List.of();
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.of(id, externalSkillId, libelle, type, emptyPathSegments);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    AdditionalSkill domain = AdditionalSkillMapper.toDomain(entity);

    BddLogger.then("it should map with empty pathSegments");
    assertNotNull(domain);
    assertNotNull(domain.getPathSegments());
    assertTrue(domain.getPathSegments().isEmpty());
  }

  @Test
  void shouldPreserveAllFieldsInRoundTripMapping() {
    BddLogger.given("a complete domain model");
    AdditionalSkill originalDomain =
        AdditionalSkill.toDomain(
            id, externalSkillId, libelle, type, pathSegments, createdAt, updatedAt);

    BddLogger.when("mapping to entity and back to domain");
    AdditionalSkillEntity entity = AdditionalSkillMapper.fromDomain(originalDomain);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);
    AdditionalSkill resultDomain = AdditionalSkillMapper.toDomain(entity);

    BddLogger.then("all fields should be preserved");
    assertNotNull(resultDomain);
    assertEquals(originalDomain.getId(), resultDomain.getId());
    assertEquals(originalDomain.getExternalSkillId(), resultDomain.getExternalSkillId());
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
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.of(id, externalSkillId, libelle, type, multipleSegments);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);

    BddLogger.when("mapping to domain");
    AdditionalSkill domain = AdditionalSkillMapper.toDomain(entity);

    BddLogger.then("all segments should be preserved");
    assertNotNull(domain);
    assertEquals(multipleSegments.size(), domain.getPathSegments().size());
    assertEquals(multipleSegments, domain.getPathSegments());
  }

  @Test
  void shouldMapDifferentSkillTypes() {
    BddLogger.given("entities with different skill types");
    EAdditionalSkillType[] types = {
      EAdditionalSkillType.ROME4,
      EAdditionalSkillType.XXI,
      EAdditionalSkillType.CASOC,
      EAdditionalSkillType.CASOL
    };

    for (EAdditionalSkillType skillType : types) {
      BddLogger.when("mapping entity with type " + skillType);
      AdditionalSkillEntity entity =
          AdditionalSkillEntity.of(id, externalSkillId, libelle, skillType, pathSegments);
      entity.setCreatedAt(createdAt);
      entity.setUpdatedAt(updatedAt);

      AdditionalSkill domain = AdditionalSkillMapper.toDomain(entity);

      BddLogger.then("the type should be correctly mapped");
      assertEquals(skillType, domain.getType());
    }
  }
}
