package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.converter;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathSegmentsConverterTest {

  private PathSegmentsConverter converter;

  @BeforeEach
  void setUp() {
    converter = new PathSegmentsConverter();
  }

  @Test
  void shouldConvertListToDatabase() {
    BddLogger.given("a list of path segments");
    List<String> pathSegments = List.of("Domain", "Issue", "MacroSkill", "Skill");

    BddLogger.when("converting to database column");
    String result = converter.convertToDatabaseColumn(pathSegments);

    BddLogger.then("it should return a joined string with separator");
    assertNotNull(result);
    assertEquals("Domain > Issue > MacroSkill > Skill", result);
  }

  @Test
  void shouldConvertSingleElementListToDatabase() {
    BddLogger.given("a list with a single path segment");
    List<String> pathSegments = List.of("Domain");

    BddLogger.when("converting to database column");
    String result = converter.convertToDatabaseColumn(pathSegments);

    BddLogger.then("it should return the single element");
    assertNotNull(result);
    assertEquals("Domain", result);
  }

  @Test
  void shouldReturnNullWhenListIsEmpty() {
    BddLogger.given("an empty list");
    List<String> pathSegments = List.of();

    BddLogger.when("converting to database column");
    String result = converter.convertToDatabaseColumn(pathSegments);

    BddLogger.then("it should return null");
    assertNull(result);
  }

  @Test
  void shouldReturnNullWhenListIsNull() {
    BddLogger.given("a null list");
    List<String> pathSegments = null;

    BddLogger.when("converting to database column");
    String result = converter.convertToDatabaseColumn(pathSegments);

    BddLogger.then("it should return null");
    assertNull(result);
  }

  @Test
  void shouldConvertStringToList() {
    BddLogger.given("a database string with separators");
    String dbData = "Domain > Issue > MacroSkill > Skill";

    BddLogger.when("converting to entity attribute");
    List<String> result = converter.convertToEntityAttribute(dbData);

    BddLogger.then("it should return a list of segments");
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("Domain", result.get(0));
    assertEquals("Issue", result.get(1));
    assertEquals("MacroSkill", result.get(2));
    assertEquals("Skill", result.get(3));
  }

  @Test
  void shouldConvertSingleSegmentStringToList() {
    BddLogger.given("a database string without separator");
    String dbData = "Domain";

    BddLogger.when("converting to entity attribute");
    List<String> result = converter.convertToEntityAttribute(dbData);

    BddLogger.then("it should return a list with one element");
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Domain", result.get(0));
  }

  @Test
  void shouldReturnEmptyListWhenStringIsEmpty() {
    BddLogger.given("an empty string");
    String dbData = "";

    BddLogger.when("converting to entity attribute");
    List<String> result = converter.convertToEntityAttribute(dbData);

    BddLogger.then("it should return an empty list");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnEmptyListWhenStringIsNull() {
    BddLogger.given("a null string");
    String dbData = null;

    BddLogger.when("converting to entity attribute");
    List<String> result = converter.convertToEntityAttribute(dbData);

    BddLogger.then("it should return an empty list");
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldPerformRoundTripConversion() {
    BddLogger.given("a list of path segments");
    List<String> originalSegments = List.of("Domain", "Issue", "MacroSkill");

    BddLogger.when("converting to database and back to entity");
    String dbValue = converter.convertToDatabaseColumn(originalSegments);
    List<String> result = converter.convertToEntityAttribute(dbValue);

    BddLogger.then("it should return the original list");
    assertNotNull(result);
    assertEquals(originalSegments.size(), result.size());
    assertEquals(originalSegments.get(0), result.get(0));
    assertEquals(originalSegments.get(1), result.get(1));
    assertEquals(originalSegments.get(2), result.get(2));
  }

  @Test
  void shouldHandleSegmentsWithSpaces() {
    BddLogger.given("a list with segments containing spaces");
    List<String> pathSegments = List.of("Domain Name", "Issue Type", "Macro Skill");

    BddLogger.when("converting to database and back");
    String dbValue = converter.convertToDatabaseColumn(pathSegments);
    List<String> result = converter.convertToEntityAttribute(dbValue);

    BddLogger.then("it should preserve the spaces in segments");
    assertNotNull(result);
    assertEquals(pathSegments.size(), result.size());
    assertEquals("Domain Name", result.get(0));
    assertEquals("Issue Type", result.get(1));
    assertEquals("Macro Skill", result.get(2));
  }
}
