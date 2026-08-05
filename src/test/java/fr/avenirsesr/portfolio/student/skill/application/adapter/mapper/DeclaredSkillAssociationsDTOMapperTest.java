package fr.avenirsesr.portfolio.student.skill.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillAssociationsDTO;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredSkillAssociationsDTOMapperTest {

  @Spy
  private TraceOverviewMapper traceOverviewMapper = Mappers.getMapper(TraceOverviewMapper.class);

  @Spy
  private DeclaredActivityViewDTOMapper declaredActivityViewDTOMapper =
      Mappers.getMapper(DeclaredActivityViewDTOMapper.class);

  @Spy
  private DeclaredExperienceMapper declaredExperienceMapper =
      Mappers.getMapper(DeclaredExperienceMapper.class);

  @InjectMocks private DeclaredSkillAssociationsDTOMapperImpl mapper;

  @Test
  void shouldMapEmptyAssociationsToDTO() {
    BddLogger.given("a declared skill associations data with empty lists");
    DeclaredSkillAssociationsData data =
        new DeclaredSkillAssociationsData(List.of(), List.of(), List.of());

    BddLogger.when("mapping to DeclaredSkillAssociationsDTO");
    DeclaredSkillAssociationsDTO dto = mapper.toDTO(data);

    BddLogger.then("it should return a DTO with empty lists");
    assertNotNull(dto);
    assertNotNull(dto.traceAssociations());
    assertTrue(dto.traceAssociations().isEmpty());
    assertNotNull(dto.declaredActivityAssociations());
    assertTrue(dto.declaredActivityAssociations().isEmpty());
    assertNotNull(dto.declaredExperienceAssociations());
    assertTrue(dto.declaredExperienceAssociations().isEmpty());
  }

  @Test
  void shouldMapDeclaredExperienceAssociationsToDTO() {
    BddLogger.given("a declared skill associations data with a declared experience association");
    Student student = StudentFixture.create().toModel();
    DeclaredExperience experience =
        DeclaredExperience.create(
            student,
            "Backend Developer",
            EExperienceType.PROFESSIONAL,
            "Tech Corp",
            "Informatique",
            "Paris",
            "Développement d'APIs",
            null,
            null,
            null,
            LocalDate.of(2022, 1, 10),
            null);
    UUID associationId = UUID.randomUUID();
    DeclaredExperienceAssociationData experienceAssociationData =
        new DeclaredExperienceAssociationData(associationId, experience);
    DeclaredSkillAssociationsData data =
        new DeclaredSkillAssociationsData(List.of(), List.of(), List.of(experienceAssociationData));

    BddLogger.when("mapping to DeclaredSkillAssociationsDTO");
    DeclaredSkillAssociationsDTO dto = mapper.toDTO(data);

    BddLogger.then("it should return a DTO exposing the declared experience association");
    assertNotNull(dto);
    assertEquals(1, dto.declaredExperienceAssociations().size());
    var experienceAssociationDTO = dto.declaredExperienceAssociations().getFirst();
    assertEquals(associationId, experienceAssociationDTO.associationId());
    DeclaredExperienceViewDTO experienceDTO = experienceAssociationDTO.declaredExperience();
    assertEquals(experience.getId(), experienceDTO.id());
    assertEquals("Backend Developer", experienceDTO.title());
    assertEquals(EExperienceType.PROFESSIONAL, experienceDTO.experienceType());
    assertEquals(LocalDate.of(2022, 1, 10), experienceDTO.startDate());
    assertNull(experienceDTO.endDate());
  }
}
