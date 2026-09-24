package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.file.application.adapter.client.FileClient;
import fr.avenirsesr.portfolio.common.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.common.file.application.adapter.request.FileUploadRequest;
import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

  private static final byte[] CONTENT = "<html></html>".getBytes(StandardCharsets.UTF_8);

  @Mock private FileClient fileClient;

  @InjectMocks private FileController fileController;

  private static FileDTO uploadedFile() {
    return new FileDTO(
        UUID.randomUUID(),
        "cgu.html",
        EFileType.HTML,
        CONTENT.length,
        "/storage/cgu",
        Instant.now());
  }

  @Test
  void shouldUploadTheMultipartContentThroughTheFileClient() {
    BddLogger.given("a multipart html file");
    MockMultipartFile file = new MockMultipartFile("file", "cgu.html", "text/html", CONTENT);
    FileDTO uploaded = uploadedFile();
    when(fileClient.upload(any())).thenReturn(uploaded);

    BddLogger.when("posting it on the files endpoint");
    ResponseEntity<FileDTO> response = fileController.upload(file, false);

    BddLogger.then("the file client is handed the content and the created file is returned");
    ArgumentCaptor<FileUploadRequest> captor = ArgumentCaptor.forClass(FileUploadRequest.class);
    verify(fileClient).upload(captor.capture());

    assertThat(captor.getValue().fileName()).isEqualTo("cgu.html");
    assertThat(captor.getValue().mimeType()).isEqualTo("text/html");
    assertThat(captor.getValue().content()).isEqualTo(CONTENT);
    assertThat(captor.getValue().isRestricted()).isFalse();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(uploaded);
  }

  @Test
  void shouldForwardTheRestrictedFlag() {
    BddLogger.given("a multipart html file to store as restricted");
    MockMultipartFile file = new MockMultipartFile("file", "cgu.html", "text/html", CONTENT);
    when(fileClient.upload(any())).thenReturn(uploadedFile());

    BddLogger.when("posting it on the files endpoint");
    fileController.upload(file, true);

    BddLogger.then("the restricted flag reaches the file client");
    ArgumentCaptor<FileUploadRequest> captor = ArgumentCaptor.forClass(FileUploadRequest.class);
    verify(fileClient).upload(captor.capture());

    assertThat(captor.getValue().isRestricted()).isTrue();
  }
}
