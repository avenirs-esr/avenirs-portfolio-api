package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TracesSummaryDTO;
import fr.avenirsesr.portfolio.student.trace.domain.data.TracesSummaryData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TracesSummaryMapperTest {

  private final TracesSummaryMapper mapper = Mappers.getMapper(TracesSummaryMapper.class);

  @Test
  void shouldMapTracesSummaryDataToDTO() {
    BddLogger.given("a traces summary data");
    TracesSummaryData data = new TracesSummaryData(5, 3, 2, 1);

    BddLogger.when("mapping to TracesSummaryDTO");
    TracesSummaryDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map all counts correctly");
    assertNotNull(dto);
    assertEquals(5, dto.associated());
    assertEquals(3, dto.unassociated());
    assertEquals(2, dto.totalWarnings());
    assertEquals(1, dto.totalCriticals());
  }
}
