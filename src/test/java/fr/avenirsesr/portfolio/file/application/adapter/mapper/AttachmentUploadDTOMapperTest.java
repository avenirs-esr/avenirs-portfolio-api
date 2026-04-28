package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AttachmentUploadDTOMapperTest {

  private final AttachmentUploadDTOMapper mapper =
      Mappers.getMapper(AttachmentUploadDTOMapper.class);

  @Test
  void shouldMapTraceAttachmentToDTO() {
    BddLogger.given("a trace attachment");
    User user = UserFixture.create().toModel();
    Trace trace = TraceFixture.create().withUser(user).toModel();
    UUID id = UUID.randomUUID();
    long size = 1024L;
    TraceAttachment attachment =
        TraceAttachment.toDomain(
            id,
            trace,
            "document.pdf",
            EFileType.PDF,
            size,
            1,
            true,
            "attachments/document.pdf",
            user,
            Instant.now(),
            Instant.now(),
            Instant.now());

    BddLogger.when("mapping to AttachmentUploadDTO");
    AttachmentUploadDTO dto = mapper.fromDomain(attachment);

    BddLogger.then("it should rename name to fileName and size to fileSize");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("document.pdf", dto.fileName());
    assertEquals(size, dto.fileSize());
    assertEquals(1, dto.version());
  }

  @Test
  void shouldReturnNullWhenOptionalIsEmpty() {
    BddLogger.given("an empty Optional");

    BddLogger.when("mapping an empty Optional to AttachmentUploadDTO");
    AttachmentUploadDTO dto = mapper.fromDomain(Optional.empty());

    BddLogger.then("it should return null");
    assertNull(dto);
  }

  @Test
  void shouldMapOptionalTraceAttachment() {
    BddLogger.given("a non-empty Optional of trace attachment");
    User user = UserFixture.create().toModel();
    Trace trace = TraceFixture.create().withUser(user).toModel();
    TraceAttachment attachment =
        TraceAttachment.toDomain(
            UUID.randomUUID(),
            trace,
            "photo.png",
            EFileType.PNG,
            512L,
            1,
            true,
            "attachments/photo.png",
            user,
            Instant.now(),
            Instant.now(),
            Instant.now());

    BddLogger.when("mapping an Optional attachment to AttachmentUploadDTO");
    AttachmentUploadDTO dto = mapper.fromDomain(Optional.of(attachment));

    BddLogger.then("it should return the mapped DTO");
    assertNotNull(dto);
    assertEquals("photo.png", dto.fileName());
  }
}
