package fr.avenirsesr.portfolio.student.skill.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredSkillProgressMapperTest {

  @Spy
  private TraceOverviewMapper traceOverviewMapper = Mappers.getMapper(TraceOverviewMapper.class);

  @InjectMocks private DeclaredSkillProgressMapperImpl mapper;

  @Test
  void shouldMapDeclaredSkillProgressToDTO() {
    BddLogger.given("a declared skill progress");
    Student student = StudentFixture.create().toModel();
    DeclaredSkill skill =
        DeclaredSkill.create(
            UUID.randomUUID(),
            "Java Programming",
            EExternalSkillType.ROME4,
            List.of("Technology", "Backend"));
    DeclaredSkillProgress skillProgress =
        DeclaredSkillProgress.create(student, skill, EDeclaredSkillLevel.BEGINNER, "My reflection");

    BddLogger.when("mapping to DeclaredSkillProgressDTO");
    DeclaredSkillProgressDTO dto = mapper.toDeclaredSkillProgressDTO(skillProgress);

    BddLogger.then("it should map skill fields correctly");
    assertNotNull(dto);
    assertEquals(skillProgress.getId(), dto.id());
    assertEquals("Java Programming", dto.title());
    assertEquals(EExternalSkillType.ROME4, dto.type());
    assertEquals(EDeclaredSkillLevel.BEGINNER, dto.level());
    assertFalse(dto.valorized());
  }
}
