package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fixtures.AMSFixture;
import fixtures.ProgramFixture;
import fixtures.ProgramProgressFixture;
import fixtures.SkillFixture;
import fixtures.SkillLevelFixture;
import fixtures.TraceFixture;
import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.api.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class TraceServiceImplTest {
  @Mock private TraceRepository traceRepository;

  @Mock private ConfigurationService configurationService;

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

  @Test
  void givenPageAndPageSize_shouldGetUnassociatedTraces() {
    // Given
    int pageNumber = 1;
    int pageSize = 8;
    int totalElement = 13;
    TraceConfigurationInfo traceConfigurationInfo = new TraceConfigurationInfo(90, 30, 5);

    Pageable pageable = PageRequest.of(pageNumber, pageSize);

    List<TraceEntity> traceEntities =
        List.of(
            TraceMapper.fromDomain(
                TraceFixture.create()
                    .withUser(student.getUser())
                    .withCreatedAt(Instant.now().minus(83, ChronoUnit.DAYS))
                    .toModel()),
            TraceMapper.fromDomain(
                TraceFixture.create()
                    .withUser(student.getUser())
                    .withCreatedAt(Instant.now().minus(84, ChronoUnit.DAYS))
                    .toModel()),
            TraceMapper.fromDomain(
                TraceFixture.create()
                    .withUser(student.getUser())
                    .withCreatedAt(Instant.now().minus(85, ChronoUnit.DAYS))
                    .toModel()),
            TraceMapper.fromDomain(
                TraceFixture.create()
                    .withUser(student.getUser())
                    .withCreatedAt(Instant.now().minus(86, ChronoUnit.DAYS))
                    .toModel()),
            TraceMapper.fromDomain(
                TraceFixture.create()
                    .withUser(student.getUser())
                    .withCreatedAt(Instant.now().minus(87, ChronoUnit.DAYS))
                    .toModel()));

    Page<TraceEntity> traceEntityPage = new PageImpl<>(traceEntities, pageable, totalElement);

    // When
    when(traceRepository.findAllUnassociatedPage(student.getUser(), pageNumber, pageSize))
        .thenReturn(traceEntityPage);
    when(configurationService.getTraceConfiguration()).thenReturn(traceConfigurationInfo);
    TraceView traceView =
        traceService.getUnassociatedTraces(student.getUser(), pageNumber, pageSize);

    // Then
    assertEquals(traceView.traces().size(), traceEntities.size());
    assertEquals(traceView.criticalCount(), 3);
    assertEquals(traceView.page().pageSize(), traceEntityPage.getSize());
    assertEquals(traceView.page().totalElements(), traceEntityPage.getTotalElements());
    assertEquals(traceView.page().totalPages(), traceEntityPage.getTotalPages());
    assertEquals(traceView.page().number(), traceEntityPage.getNumber());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldDeleteTraceAndLinksToAmsAndSkillLevels() {
    // Given
    AMS ams = AMSFixture.create().toModel();
    Skill skill = SkillFixture.create().withSkillLevels(1).toModel();
    SkillLevel skillLevel = SkillLevelFixture.create().withSkill(skill).toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevel))
            .withAmses(List.of(ams))
            .toModel();

    // When
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    traceService.deleteById(student.getUser(), trace.getId());

    // Then
    verify(traceRepository).deleteById(trace.getId());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldThrowTraceNotFoundException() {
    // Given
    AMS ams = AMSFixture.create().toModel();
    Skill skill = SkillFixture.create().withSkillLevels(1).toModel();
    SkillLevel skillLevel = SkillLevelFixture.create().withSkill(skill).toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevel))
            .withAmses(List.of(ams))
            .toModel();

    // When
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.empty());
    TraceNotFoundException exception =
        assertThrows(
            TraceNotFoundException.class,
            () -> traceService.deleteById(student.getUser(), trace.getId()));

    // Then
    assertEquals(EErrorCode.TRACE_NOT_FOUND, exception.getErrorCode());
    verify(traceRepository, never()).deleteById(trace.getId());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldThrowUserNotAuthorizedException() {
    // Given
    User otherUser = UserFixture.createStudent().toModel();
    AMS ams = AMSFixture.create().toModel();
    Skill skill = SkillFixture.create().withSkillLevels(1).toModel();
    SkillLevel skillLevel = SkillLevelFixture.create().withSkill(skill).toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevel))
            .withAmses(List.of(ams))
            .toModel();

    // When
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    UserNotAuthorizedException exception =
        assertThrows(
            UserNotAuthorizedException.class,
            () -> traceService.deleteById(otherUser, trace.getId()));

    // Then
    assertEquals(EErrorCode.USER_NOT_AUTHORIZED, exception.getErrorCode());
    verify(traceRepository, never()).deleteById(trace.getId());
  }

  @Test
  void givenUnassociatedTraces_shouldReturnSummary() {
    // Given
    TraceConfigurationInfo traceConfigurationInfo = new TraceConfigurationInfo(90, 30, 5);
    List<Trace> unassociatedTraces =
        List.of(
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(12, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(72, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(84, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(85, ChronoUnit.DAYS))
                .toModel());

    // When
    when(traceRepository.findAllUnassociated(student.getUser())).thenReturn(unassociatedTraces);
    when(configurationService.getTraceConfiguration()).thenReturn(traceConfigurationInfo);
    UnassociatedTracesSummary summary =
        traceService.getUnassociatedTracesSummary(student.getUser());

    // Then
    assertEquals(4, summary.total());
    assertEquals(3, summary.totalWarnings());
    assertEquals(1, summary.totalCriticals());
  }
}
