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
    BddLogger.given("a trace view data with an optional deletion date");
    UUID id = UUID.randomUUID();
    LocalDate deletionDate = LocalDate.now().plusDays(30);
    TraceViewData data =
        new TraceViewData(
            id, "My Trace", true, Instant.now(), Instant.now(), Optional.of(deletionDate));

    BddLogger.when("mapping to TraceViewDTO");
    TraceViewDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map all fields and unwrap Optional deletion date");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("My Trace", dto.title());
    assertTrue(dto.isAssociated());
    assertEquals(deletionDate, dto.willBeDeletedAt());
  }

  @Test
  void shouldMapEmptyOptionalDeletionDateToNull() {
    BddLogger.given("a trace view data without a deletion date");
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

    BddLogger.then("it should return null for willBeDeletedAt");
    assertNotNull(dto);
    assertNull(dto.willBeDeletedAt());
  }

  @Test
  void shouldMapListOfTraceViewDataToDTOs() {
    BddLogger.given("a list of trace view data");
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

    BddLogger.then("it should return a list of the same size");
    assertNotNull(dtos);
    assertEquals(2, dtos.size());
  }
}
