package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.converter.PathSegmentsConverter;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AdditionalSkillEntityTest {

  private final UUID externalSkillId = UUID.randomUUID();
  private final String libelle = "Java Programming";
  private final EExternalSkillType type = EExternalSkillType.ROME4;
  private final List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");

  @Test
  void shouldCreateEntityWithOfMethod() {
    BddLogger.given("valid entity data with ID");
    UUID id = UUID.randomUUID();

    BddLogger.when("creating entity with of method");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.of(id, externalSkillId, libelle, type, pathSegments);

    BddLogger.then("it should create an entity with all fields set");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(externalSkillId, entity.getExternalSkillId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
    assertEquals(pathSegments, entity.getPathSegments());
  }

  @Test
  void shouldCreateEntityWithCreateMethod() {
    BddLogger.given("valid entity data without ID");

    BddLogger.when("creating entity with create method");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.create(externalSkillId, libelle, type, pathSegments);

    BddLogger.then("it should generate an ID and set all fields");
    assertNotNull(entity);
    assertNotNull(entity.getId());
    assertEquals(externalSkillId, entity.getExternalSkillId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
    assertEquals(pathSegments, entity.getPathSegments());
  }

  @Test
  void shouldGenerateIdAutomatically() {
    BddLogger.given("valid entity data");

    BddLogger.when("creating entity with create method");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.create(externalSkillId, libelle, type, pathSegments);

    BddLogger.then("it should generate a non-null ID");
    assertNotNull(entity.getId());
    assertNotEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), entity.getId());
  }

  @Test
  void shouldSetAllFieldsWithSetters() {
    BddLogger.given("an entity instance");
    AdditionalSkillEntity entity = new AdditionalSkillEntity();

    BddLogger.when("setting all fields with setters");
    UUID id = UUID.randomUUID();
    UUID newExternalSkillId = UUID.randomUUID();
    String newLibelle = "Python Programming";
    EExternalSkillType newType = EExternalSkillType.XXI;
    List<String> newPathSegments = List.of("A", "B", "C");

    entity.setId(id);
    entity.setExternalSkillId(newExternalSkillId);
    entity.setLibelle(newLibelle);
    entity.setType(newType);
    entity.setPathSegments(newPathSegments);

    BddLogger.then("all fields should be set correctly");
    assertEquals(id, entity.getId());
    assertEquals(newExternalSkillId, entity.getExternalSkillId());
    assertEquals(newLibelle, entity.getLibelle());
    assertEquals(newType, entity.getType());
    assertEquals(newPathSegments, entity.getPathSegments());
  }

  @Test
  void shouldUseNoArgsConstructor() {
    BddLogger.given("JPA requires a no-args constructor");

    BddLogger.when("creating entity with no-args constructor");
    AdditionalSkillEntity entity = new AdditionalSkillEntity();

    BddLogger.then("it should create an instance with null fields");
    assertNotNull(entity);
    assertNull(entity.getId());
    assertNull(entity.getExternalSkillId());
    assertNull(entity.getLibelle());
    assertNull(entity.getType());
    assertNull(entity.getPathSegments());
  }

  @Test
  void shouldHandleNullPathSegments() {
    BddLogger.given("entity data with null pathSegments");
    UUID id = UUID.randomUUID();

    BddLogger.when("creating entity with null pathSegments");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.of(id, externalSkillId, libelle, type, null);

    BddLogger.then("it should convert null pathSegments to empty list");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(externalSkillId, entity.getExternalSkillId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
    assertNotNull(entity.getPathSegments());
    assertTrue(entity.getPathSegments().isEmpty());
  }

  @Test
  void shouldHandleEmptyPathSegments() {
    BddLogger.given("entity data with empty pathSegments");
    UUID id = UUID.randomUUID();
    List<String> emptyPathSegments = List.of();

    BddLogger.when("creating entity with empty pathSegments");
    AdditionalSkillEntity entity =
        AdditionalSkillEntity.of(id, externalSkillId, libelle, type, emptyPathSegments);

    BddLogger.then("it should accept empty pathSegments");
    assertNotNull(entity);
    assertNotNull(entity.getPathSegments());
    assertTrue(entity.getPathSegments().isEmpty());
  }

  @Test
  void shouldHaveCorrectTableName() {
    BddLogger.given("an entity class");

    BddLogger.when("checking the @Table annotation");
    Table tableAnnotation = AdditionalSkillEntity.class.getAnnotation(Table.class);

    BddLogger.then("it should have the correct table name");
    assertNotNull(tableAnnotation);
    assertEquals("additional_skill", tableAnnotation.name());
  }

  @Test
  void shouldHavePathSegmentsConverterAnnotation() {
    BddLogger.given("the pathSegments field");

    BddLogger.when("checking the @Convert annotation");
    try {
      var field = AdditionalSkillEntity.class.getDeclaredField("pathSegments");
      Convert convertAnnotation = field.getAnnotation(Convert.class);

      BddLogger.then("it should have the PathSegmentsConverter");
      assertNotNull(convertAnnotation);
      assertEquals(
          PathSegmentsConverter.class,
          convertAnnotation.converter(),
          "Should use PathSegmentsConverter");
    } catch (NoSuchFieldException e) {
      fail("pathSegments field should exist");
    }
  }

  @Test
  void shouldHaveExternalSkillIdNotNullable() {
    BddLogger.given("the externalSkillId field");

    BddLogger.when("checking the @Column annotation");
    try {
      var field = AdditionalSkillEntity.class.getDeclaredField("externalSkillId");
      Column columnAnnotation = field.getAnnotation(Column.class);

      BddLogger.then("it should be marked as not nullable");
      assertNotNull(columnAnnotation);
      assertFalse(columnAnnotation.nullable(), "externalSkillId should not be nullable");
      assertEquals("external_skill_id", columnAnnotation.name());
    } catch (NoSuchFieldException e) {
      fail("externalSkillId field should exist");
    }
  }

  @Test
  void shouldStoreTypeAsString() {
    BddLogger.given("the type field");

    BddLogger.when("checking the @Enumerated annotation");
    try {
      var field = AdditionalSkillEntity.class.getDeclaredField("type");
      Enumerated enumeratedAnnotation = field.getAnnotation(Enumerated.class);

      BddLogger.then("it should use EnumType.STRING");
      assertNotNull(enumeratedAnnotation);
      assertEquals(EnumType.STRING, enumeratedAnnotation.value(), "Should store enum as STRING");
    } catch (NoSuchFieldException e) {
      fail("type field should exist");
    }
  }
}
