package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.AttachmentUploadDTOMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceDetailDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceDetailMapperTest {

  @Spy
  private AttachmentUploadDTOMapper attachmentUploadDTOMapper =
      Mappers.getMapper(AttachmentUploadDTOMapper.class);

  @InjectMocks private TraceDetailMapperImpl mapper;

  @Test
  void shouldMapTraceDetailDataToDTO() {
    BddLogger.given("a trace detail data without attachment");
    UUID id = UUID.randomUUID();
    TraceDetailData data =
        new TraceDetailData(
            id,
            "My Trace",
            true,
            "Program Name",
            false,
            null,
            "Personal note",
            Optional.of("https://example.com"),
            Optional.empty(),
            Instant.now(),
            Instant.now());

    BddLogger.when("mapping to TraceDetailDTO");
    TraceDetailDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map all fields correctly");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("My Trace", dto.title());
    assertTrue(dto.isAssociated());
    assertEquals("Program Name", dto.programName());
    assertEquals("https://example.com", dto.link());
    assertNull(dto.attachment());
  }
}
