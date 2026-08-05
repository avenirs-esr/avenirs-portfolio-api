package fr.avenirsesr.portfolio.student.association.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredActivityDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredExperienceDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredSkillIDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultTraceDTO;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AssociationSearchResultDTOMapperTest {

  private final AssociationSearchResultDTOMapper mapper =
      Mappers.getMapper(AssociationSearchResultDTOMapper.class);

  @Test
  void shouldMapToDeclaredActivityDTO() {
    BddLogger.given("an association search result for a declared activity");
    UUID id = UUID.randomUUID();
    AssociationSearchResultData data =
        new AssociationSearchResultData(
            id, "Java Workshop", EActivityThematic.EXPERIENCES.name(), false);

    BddLogger.when("mapping to DeclaredActivityDTO");
    AssociationSearchResultDeclaredActivityDTO dto = mapper.toDeclaredActivityDTO(data);

    BddLogger.then("it should return correct fields with parsed thematic");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("Java Workshop", dto.title());
    assertEquals(EActivityThematic.EXPERIENCES, dto.thematic());
    assertFalse(dto.disabled());
  }

  @Test
  void shouldMapToDeclaredSkillDTO() {
    BddLogger.given("an association search result for a declared skill");
    UUID id = UUID.randomUUID();
    AssociationSearchResultData data =
        new AssociationSearchResultData(
            id, "Java Programming", EExternalSkillType.ROME4.name(), false);

    BddLogger.when("mapping to DeclaredSkillDTO");
    AssociationSearchResultDeclaredSkillIDTO dto = mapper.toDeclaredSkillDTO(data);

    BddLogger.then("it should return correct fields with parsed skill type");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals(EExternalSkillType.ROME4, dto.type());
  }

  @Test
  void shouldMapToDeclaredExperienceDTO() {
    BddLogger.given("an association search result for a declared experience");
    UUID id = UUID.randomUUID();
    AssociationSearchResultData data =
        new AssociationSearchResultData(
            id, "Professional experience", EExperienceType.PROFESSIONAL.name(), true);

    BddLogger.when("mapping to DeclaredExperienceDTO");
    AssociationSearchResultDeclaredExperienceDTO dto = mapper.toDeclaredExperienceDTO(data);

    BddLogger.then("it should return correct fields with parsed experience type");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals(EExperienceType.PROFESSIONAL, dto.experienceType());
    assertTrue(dto.disabled());
  }

  @Test
  void shouldMapToTraceDTO() {
    BddLogger.given("an association search result for a trace");
    UUID id = UUID.randomUUID();
    AssociationSearchResultData data = new AssociationSearchResultData(id, "My Trace", null, false);

    BddLogger.when("mapping to TraceDTO");
    AssociationSearchResultTraceDTO dto = mapper.toTraceDTO(data);

    BddLogger.then("it should return correct fields");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("My Trace", dto.title());
    assertFalse(dto.disabled());
  }

  @Test
  void shouldHandleNullCategoryInDeclaredActivityDTO() {
    BddLogger.given("an association search result with null category");
    AssociationSearchResultData data =
        new AssociationSearchResultData(UUID.randomUUID(), "Title", null, false);

    BddLogger.when("mapping to DeclaredActivityDTO");
    AssociationSearchResultDeclaredActivityDTO dto = mapper.toDeclaredActivityDTO(data);

    BddLogger.then("it should return null for thematic");
    assertNull(dto.thematic());
  }
}
