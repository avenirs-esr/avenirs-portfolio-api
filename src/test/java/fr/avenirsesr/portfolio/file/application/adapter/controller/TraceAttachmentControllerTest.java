package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachmentDownload;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

public class TraceAttachmentControllerTest {

  @Mock private TraceAttachmentService service;
  @Mock private Principal principal;

  @InjectMocks private TraceAttachmentController controller;

  private UUID traceId;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    traceId = UUID.randomUUID();

    when(principal.getName()).thenReturn("user123");
  }

  @Test
  void uploadAttachment_success_shouldReturn201() throws IOException {
    BddLogger.given("a TraceAttachmentController");
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[10]);

    var returnedAttachment = mock(fr.avenirsesr.portfolio.file.domain.model.TraceAttachment.class);
    when(service.uploadTraceAttachment(
            eq(traceId), anyString(), anyString(), anyLong(), any(byte[].class)))
        .thenReturn(returnedAttachment);

    BddLogger.when("the attachment upload success");
    ResponseEntity<AttachmentUploadDTO> response =
        controller.uploadAttachment(principal, traceId, file);

    BddLogger.then("it should return a 201");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    verify(service)
        .uploadTraceAttachment(eq(traceId), anyString(), anyString(), anyLong(), any(byte[].class));
  }

  @Test
  void downloadAttachment_success_shouldReturn200() throws IOException {
    BddLogger.given("a TraceAttachmentController");
    UUID attachmentId = UUID.randomUUID();
    byte[] fileContent = "Test content".getBytes();
    String fileName = "test.pdf";

    when(service.downloadTraceAttachment(attachmentId))
        .thenReturn(new TraceAttachmentDownload(fileName, fileContent));

    BddLogger.when("the attachment download success");
    ResponseEntity<byte[]> response = controller.downloadAttachment(attachmentId);

    BddLogger.then("it should return a 200");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).isEqualTo(fileContent);
    assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
        .isEqualTo("attachment; filename=\"test.pdf\"");

    verify(service).downloadTraceAttachment(attachmentId);
  }
}
