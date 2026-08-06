package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceOverviewDTO;
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
  void shouldMapTraceToDTO() {
    BddLogger.given("a trace");
    Trace trace = TraceFixture.create().toModel();

    BddLogger.when("mapping trace to TraceOverviewDTO");
    TraceOverviewDTO dto = mapper.toDTO(trace);

    BddLogger.then("it should map id and title");
    assertNotNull(dto);
    assertEquals(trace.getId(), dto.id());
    assertEquals(trace.getTitle(), dto.title());
  }

  @Test
  void shouldReturnNullWhenTraceIsNull() {
    BddLogger.given("a null trace");

    BddLogger.when("mapping null trace to TraceOverviewDTO");
    TraceOverviewDTO dto = mapper.toDTO(null);

    BddLogger.then("it should return null");
    assertNull(dto);
  }
}
