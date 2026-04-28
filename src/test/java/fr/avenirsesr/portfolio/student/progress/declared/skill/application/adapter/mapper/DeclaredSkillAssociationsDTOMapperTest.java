package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import java.util.List;
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

  @InjectMocks private DeclaredSkillAssociationsDTOMapperImpl mapper;

  @Test
  void shouldMapEmptyAssociationsToDTO() {
    BddLogger.given("a declared skill associations data with empty lists");
    DeclaredSkillAssociationsData data = new DeclaredSkillAssociationsData(List.of(), List.of());

    BddLogger.when("mapping to DeclaredSkillAssociationsDTO");
    DeclaredSkillAssociationsDTO dto = mapper.toDTO(data);

    BddLogger.then("it should return a DTO with empty lists");
    assertNotNull(dto);
    assertNotNull(dto.traceAssociations());
    assertTrue(dto.traceAssociations().isEmpty());
    assertNotNull(dto.declaredActivityAssociations());
    assertTrue(dto.declaredActivityAssociations().isEmpty());
  }
}
