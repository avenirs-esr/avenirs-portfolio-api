package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeInstitution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeProgram;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeProgramProgress;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeSkill;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeSkillLevel;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeTrack;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeUser;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TrackServiceImplTest {
  @InjectMocks private TrackServiceImpl trackService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = FakeUser.create().toModel().toStudent();
  }

  @Test
  void givenTrackWithoutSkillLevels_shouldReturnLifeProject() {
    // Given
    Track track = FakeTrack.of(student.getUser()).withSkillLevel(List.of()).toModel();

    // When
    String result = trackService.programNameOfTrack(track);

    // Then
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTrackWithSkillLevelsButNoApc_shouldReturnLifeProject() {
    // Given
    Program nonApcProgram = FakeProgram.of(FakeInstitution.create().toModel()).isNotAPC().toModel();
    SkillLevel skillLevel = FakeSkillLevel.create().toModel();
    Skill skill = FakeSkill.of(Set.of(skillLevel)).toModel();
    ProgramProgress progress =
        FakeProgramProgress.of(nonApcProgram, student, Set.of(skill)).toModel();
    Track track = FakeTrack.of(student.getUser()).withSkillLevel(List.of(skillLevel)).toModel();

    // When
    String result = trackService.programNameOfTrack(track);

    // Then
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTrackWithApcProgram_shouldReturnProgramName() {
    // Given
    Program apcProgram = FakeProgram.of(FakeInstitution.create().toModel()).toModel();

    SkillLevel skillLevel = FakeSkillLevel.create().toModel();
    Skill skill = FakeSkill.of(Set.of(skillLevel)).toModel();
    ProgramProgress progress = FakeProgramProgress.of(apcProgram, student, Set.of(skill)).toModel();
    Track track = FakeTrack.of(student.getUser()).withSkillLevel(List.of(skillLevel)).toModel();

    // When
    String result = trackService.programNameOfTrack(track);

    // Then
    assertEquals(apcProgram.getName(), result);
  }
}
