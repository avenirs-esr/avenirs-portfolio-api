package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceAssociationsDTO;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationsData;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceAssociationsMapperTest {

  @Spy
  private DeclaredActivityViewDTOMapper declaredActivityViewDTOMapper =
      Mappers.getMapper(DeclaredActivityViewDTOMapper.class);

  @Mock private DeclaredSkillProgressMapper declaredSkillProgressMapper;

  @Mock private DeclaredExperienceMapper declaredExperienceMapper;

  @InjectMocks private TraceAssociationsMapperImpl mapper;

  @Test
  void shouldMapEmptyAssociationsToDTO() {
    BddLogger.given("a trace associations data with empty lists");
    TraceAssociationsData data = new TraceAssociationsData(List.of(), List.of(), List.of());

    BddLogger.when("mapping to TraceAssociationsDTO");
    TraceAssociationsDTO dto = mapper.toDTO(data);

    BddLogger.then("it should return a DTO with empty lists");
    assertNotNull(dto);
    assertNotNull(dto.declaredActivityAssociations());
    assertTrue(dto.declaredActivityAssociations().isEmpty());
    assertNotNull(dto.declaredSkillAssociations());
    assertTrue(dto.declaredSkillAssociations().isEmpty());
    assertNotNull(dto.declaredExperienceAssociations());
    assertTrue(dto.declaredExperienceAssociations().isEmpty());
  }
}
