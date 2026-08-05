package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityAssociationsDTOMapperTest {

  @Spy
  private TraceOverviewMapper traceOverviewMapper = Mappers.getMapper(TraceOverviewMapper.class);

  @Mock private DeclaredSkillProgressMapper declaredSkillProgressMapper;

  @InjectMocks private DeclaredActivityAssociationsDTOMapperImpl mapper;

  @Test
  void shouldMapEmptyAssociationsToDTO() {
    BddLogger.given("a declared activity associations data with empty lists");
    DeclaredActivityAssociationsData data =
        new DeclaredActivityAssociationsData(List.of(), List.of());

    BddLogger.when("mapping to DeclaredActivityAssociationsDTO");
    DeclaredActivityAssociationsDTO dto = mapper.toDTO(data);

    BddLogger.then("it should return a DTO with empty lists");
    assertNotNull(dto);
    assertNotNull(dto.traceAssociations());
    assertTrue(dto.traceAssociations().isEmpty());
    assertNotNull(dto.declaredSkillAssociations());
    assertTrue(dto.declaredSkillAssociations().isEmpty());
  }
}
