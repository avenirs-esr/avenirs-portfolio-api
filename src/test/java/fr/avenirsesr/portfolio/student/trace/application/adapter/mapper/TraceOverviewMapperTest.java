package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceWithProjectNameData;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.infrastructure.fixture.TraceFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceOverviewMapperTest {

  @Spy private OptionalMapper optionalMapper = Mappers.getMapper(OptionalMapper.class);

  @InjectMocks private TraceOverviewMapperImpl mapper;

  @Test
  void shouldMapTraceAndProgramNameToDTO() {
    BddLogger.given("a trace and a program name");
    Trace trace = TraceFixture.create().toModel();

    BddLogger.when("mapping with programName to TraceOverviewDTO");
    TraceOverviewDTO dto = mapper.toDTO(trace, "My Program");

    BddLogger.then("it should map id, title, and programName");
    assertNotNull(dto);
    assertEquals(trace.getId(), dto.id());
    assertEquals(trace.getTitle(), dto.title());
    assertEquals("My Program", dto.programName());
  }

  @Test
  void shouldMapTraceAloneWithNullProgramName() {
    BddLogger.given("a trace without a program name");
    Trace trace = TraceFixture.create().toModel();

    BddLogger.when("mapping trace only to TraceOverviewDTO");
    TraceOverviewDTO dto = mapper.toDTO(trace);

    BddLogger.then("it should map id with null programName");
    assertNotNull(dto);
    assertEquals(trace.getId(), dto.id());
    assertNull(dto.programName());
  }

  @Test
  void shouldReturnNullWhenTraceIsNull() {
    BddLogger.given("a null trace");

    BddLogger.when("mapping null trace to TraceOverviewDTO");
    TraceOverviewDTO dto = mapper.toDTO((Trace) null);

    BddLogger.then("it should return null");
    assertNull(dto);
  }

  @Test
  void shouldMapTraceWithProjectNameData() {
    BddLogger.given("a TraceWithProjectNameData");
    Trace trace = TraceFixture.create().toModel();
    TraceWithProjectNameData data = new TraceWithProjectNameData(trace, "Project X");

    BddLogger.when("mapping TraceWithProjectNameData to TraceOverviewDTO");
    TraceOverviewDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map trace and program name");
    assertNotNull(dto);
    assertEquals(trace.getId(), dto.id());
    assertEquals("Project X", dto.programName());
  }
}
