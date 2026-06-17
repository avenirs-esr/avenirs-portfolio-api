package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceViewData;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class TraceViewMapperTest {

  private final TraceViewMapper mapper = Mappers.getMapper(TraceViewMapper.class);

  @Test
  void shouldMapTraceViewDataToDTO() {
    BddLogger.given("a trace view data with locked declared activities");

    UUID id = UUID.randomUUID();
    LocalDate deletionDate = LocalDate.now().plusDays(30);
    UUID activityId = UUID.randomUUID();
    Instant createdAt = Instant.now();
    Instant updatedAt = Instant.now();

    TraceViewData data =
        new TraceViewData(id, "My Trace", true, createdAt, updatedAt, Optional.of(deletionDate));

    BddLogger.when("mapping to TraceViewDTO");
    TraceViewDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map all fields");

    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("My Trace", dto.title());
    assertTrue(dto.isAssociated());
    assertEquals(createdAt, dto.createdAt());
    assertEquals(updatedAt, dto.updatedAt());
  }

  @Test
  void shouldMapTraceViewDataWithEmptyLockedDeclaredActivities() {
    BddLogger.given("a trace view data without locked declared activities");

    TraceViewData data =
        new TraceViewData(
            UUID.randomUUID(),
            "Other Trace",
            false,
            Instant.now(),
            Instant.now(),
            Optional.empty());

    BddLogger.when("mapping to TraceViewDTO");
    TraceViewDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map an empty locked declared activities list");

    assertNotNull(dto);
    assertFalse(dto.isAssociated());
  }

  @Test
  void shouldMapListOfTraceViewDataToDTOs() {
    BddLogger.given("a list of trace view data");

    UUID firstActivityId = UUID.randomUUID();

    List<TraceViewData> dataList =
        List.of(
            new TraceViewData(
                UUID.randomUUID(), "Trace A", true, Instant.now(), Instant.now(), Optional.empty()),
            new TraceViewData(
                UUID.randomUUID(),
                "Trace B",
                false,
                Instant.now(),
                Instant.now(),
                Optional.empty()));

    BddLogger.when("mapping list to DTOs");
    List<TraceViewDTO> dtos = mapper.toDTOs(dataList);

    BddLogger.then("it should return a list of the same size and map locked declared activities");

    assertNotNull(dtos);
    assertEquals(2, dtos.size());

    assertTrue(dtos.getFirst().isAssociated());

    assertFalse(dtos.get(1).isAssociated());
  }
}
