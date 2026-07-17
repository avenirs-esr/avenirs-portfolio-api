package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceViewData;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceViewMapperTest {

  @Spy private FileDtoMapper fileDtoMapper = Mappers.getMapper(FileDtoMapper.class);

  @InjectMocks private TraceViewMapperImpl mapper;

  @Test
  void shouldMapTraceViewDataToDTO() {
    BddLogger.given("a trace view data with locked declared activities");

    UUID id = UUID.randomUUID();
    LocalDate deletionDate = LocalDate.now().plusDays(30);
    UUID activityId = UUID.randomUUID();
    Instant createdAt = Instant.now();
    Instant updatedAt = Instant.now();

    TraceViewData data =
        new TraceViewData(
            id,
            "My Trace",
            true,
            createdAt,
            updatedAt,
            Optional.of(deletionDate),
            Optional.empty(),
            ETraceAuthorType.PERSONAL,
            "My personal note",
            "My AI use justification");

    BddLogger.when("mapping to TraceViewDTO");
    TraceViewDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map all fields");

    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("My Trace", dto.title());
    assertTrue(dto.isAssociated());
    assertEquals(createdAt, dto.createdAt());
    assertEquals(updatedAt, dto.updatedAt());
    assertNull(dto.attachment());
    assertEquals(ETraceAuthorType.PERSONAL, dto.authorType());
    assertEquals("My personal note", dto.personalNote());
    assertEquals("My AI use justification", dto.aiUseJustification());
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
            Optional.empty(),
            Optional.empty(),
            ETraceAuthorType.COLLECTIVE,
            null,
            null);

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
                UUID.randomUUID(),
                "Trace A",
                true,
                Instant.now(),
                Instant.now(),
                Optional.empty(),
                Optional.empty(),
                ETraceAuthorType.PERSONAL,
                null,
                null),
            new TraceViewData(
                UUID.randomUUID(),
                "Trace B",
                false,
                Instant.now(),
                Instant.now(),
                Optional.empty(),
                Optional.empty(),
                ETraceAuthorType.THIRD_PARTY,
                null,
                null));

    BddLogger.when("mapping list to DTOs");
    List<TraceViewDTO> dtos = mapper.toDTOs(dataList);

    BddLogger.then("it should return a list of the same size and map locked declared activities");

    assertNotNull(dtos);
    assertEquals(2, dtos.size());

    assertTrue(dtos.getFirst().isAssociated());

    assertFalse(dtos.get(1).isAssociated());
  }
}
