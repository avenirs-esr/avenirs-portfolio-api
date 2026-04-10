package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.converter.PathSegmentsConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DeclaredSkillEntityTest {

  private final UUID id = UUID.randomUUID();
  private final String libelle = "Java Programming";
  private final EExternalSkillType type = EExternalSkillType.ROME4;
  private final List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");

  @Test
  void shouldCreateEntityWithOfMethod() {
    BddLogger.given("valid entity data");

    BddLogger.when("creating entity with of method");
    DeclaredSkillEntity entity = DeclaredSkillEntity.of(id, libelle, type, pathSegments);

    BddLogger.then("it should create an entity with all fields set");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
    assertEquals(pathSegments, entity.getPathSegments());
  }

  @Test
  void shouldSetAllFieldsWithSetters() {
    BddLogger.given("an entity instance");
    DeclaredSkillEntity entity = new DeclaredSkillEntity();

    BddLogger.when("setting all fields with setters");
    UUID newId = UUID.randomUUID();
    String newLibelle = "Python Programming";
    EExternalSkillType newType = EExternalSkillType.XXI;
    List<String> newPathSegments = List.of("A", "B", "C");

    entity.setId(newId);
    entity.setLibelle(newLibelle);
    entity.setType(newType);
    entity.setPathSegments(newPathSegments);

    BddLogger.then("all fields should be set correctly");
    assertEquals(newId, entity.getId());
    assertEquals(newLibelle, entity.getLibelle());
    assertEquals(newType, entity.getType());
    assertEquals(newPathSegments, entity.getPathSegments());
  }

  @Test
  void shouldUseNoArgsConstructor() {
    BddLogger.given("JPA requires a no-args constructor");

    BddLogger.when("creating entity with no-args constructor");
    DeclaredSkillEntity entity = new DeclaredSkillEntity();

    BddLogger.then("it should create an instance with null fields");
    assertNotNull(entity);
    assertNull(entity.getId());
    assertNull(entity.getLibelle());
    assertNull(entity.getType());
    assertNull(entity.getPathSegments());
  }

  @Test
  void shouldHandleNullPathSegments() {
    BddLogger.given("entity data with null pathSegments");

    BddLogger.when("creating entity with null pathSegments");
    DeclaredSkillEntity entity = DeclaredSkillEntity.of(id, libelle, type, null);

    BddLogger.then("it should convert null pathSegments to empty list");
    assertNotNull(entity);
    assertEquals(id, entity.getId());
    assertEquals(libelle, entity.getLibelle());
    assertEquals(type, entity.getType());
    assertNotNull(entity.getPathSegments());
    assertTrue(entity.getPathSegments().isEmpty());
  }

  @Test
  void shouldHandleEmptyPathSegments() {
    BddLogger.given("entity data with empty pathSegments");
    List<String> emptyPathSegments = List.of();

    BddLogger.when("creating entity with empty pathSegments");
    DeclaredSkillEntity entity = DeclaredSkillEntity.of(id, libelle, type, emptyPathSegments);

    BddLogger.then("it should accept empty pathSegments");
    assertNotNull(entity);
    assertNotNull(entity.getPathSegments());
    assertTrue(entity.getPathSegments().isEmpty());
  }

  @Test
  void shouldHaveCorrectTableName() {
    BddLogger.given("an entity class");

    BddLogger.when("checking the @Table annotation");
    Table tableAnnotation = DeclaredSkillEntity.class.getAnnotation(Table.class);

    BddLogger.then("it should have the correct table name");
    assertNotNull(tableAnnotation);
    assertEquals("declared_skill", tableAnnotation.name());
  }

  @Test
  void shouldHavePathSegmentsConverterAnnotation() {
    BddLogger.given("the pathSegments field");

    BddLogger.when("checking the @Convert annotation");
    try {
      var field = DeclaredSkillEntity.class.getDeclaredField("pathSegments");
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
  void shouldStoreTypeAsString() {
    BddLogger.given("the type field");

    BddLogger.when("checking the @Enumerated annotation");
    try {
      var field = DeclaredSkillEntity.class.getDeclaredField("type");
      Enumerated enumeratedAnnotation = field.getAnnotation(Enumerated.class);

      BddLogger.then("it should use EnumType.STRING");
      assertNotNull(enumeratedAnnotation);
      assertEquals(EnumType.STRING, enumeratedAnnotation.value(), "Should store enum as STRING");
    } catch (NoSuchFieldException e) {
      fail("type field should exist");
    }
  }
}
