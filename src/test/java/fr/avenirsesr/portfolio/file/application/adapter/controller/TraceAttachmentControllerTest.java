package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

public class TraceAttachmentControllerTest {

  @Mock private UserUtil userUtil;
  @Mock private TraceAttachmentService service;
  @Mock private Principal principal;

  @InjectMocks private TraceAttachmentController controller;

  private User user;
  private Trace trace;
  private UUID traceId;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    user = mock(User.class);
    trace = mock(Trace.class);
    traceId = UUID.randomUUID();

    when(principal.getName()).thenReturn("user123");
    when(userUtil.getUser(principal)).thenReturn(user);
  }

  @Test
  void uploadAttachment_success_shouldReturn201() throws IOException {
    MockMultipartFile file =
        new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[10]);

    var returnedAttachment = mock(fr.avenirsesr.portfolio.file.domain.model.TraceAttachment.class);
    when(service.uploadTraceAttachment(
            any(), eq(traceId), anyString(), anyString(), anyLong(), any(byte[].class)))
        .thenReturn(returnedAttachment);

    ResponseEntity<AttachmentUploadDTO> response =
        controller.uploadAttachment(principal, traceId, file);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    verify(service)
        .uploadTraceAttachment(
            any(), eq(traceId), anyString(), anyString(), anyLong(), any(byte[].class));
  }
}
