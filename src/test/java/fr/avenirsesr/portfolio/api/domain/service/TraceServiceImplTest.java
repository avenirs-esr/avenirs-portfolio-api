package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fixtures.*;
import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TraceServiceImplTest {
  @InjectMocks private TraceServiceImpl traceService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void givenTraceWithoutSkillLevels_shouldReturnLifeProject() {
    // Given
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

    // When
    String result = traceService.programNameOfTrace(trace);

    // Then
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTraceWithSkillLevelsButNoApc_shouldReturnLifeProject() {
    // Given
    Program program = ProgramFixture.create().withAPC(false).toModel();
    ProgramProgress progress =
        ProgramProgressFixture.create().withProgram(program).withStudent(student).toModel();
    Skill skill = SkillFixture.create().withSkillLevels(1).withProgramProgress(progress).toModel();
    SkillLevel skillLevel = SkillLevelFixture.create().withSkill(skill).toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevel))
            .toModel();

    // When
    String result = traceService.programNameOfTrace(trace);

    // Then
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTraceWithApcProgram_shouldReturnProgramName() {
    // Given
    Program program = ProgramFixture.create().withAPC(true).toModel();
    ProgramProgress progress =
        ProgramProgressFixture.create().withProgram(program).withStudent(student).toModel();
    Skill skill = SkillFixture.create().withSkillLevels(1).withProgramProgress(progress).toModel();
    SkillLevel skillLevel = SkillLevelFixture.create().withSkill(skill).toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevel))
            .toModel();

    // When
    String result = traceService.programNameOfTrace(trace);

    // Then
    assertEquals(program.getName(), result);
  }
}
