package fr.avenirsesr.portfolio.file.infrastructure.adapter.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.common.file.application.adapter.request.FileUploadRequest;
import fr.avenirsesr.portfolio.common.file.domain.model.File;
import fr.avenirsesr.portfolio.common.file.domain.model.FileContent;
import fr.avenirsesr.portfolio.common.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.common.file.infrastructure.adapter.client.LocalFileClient;
import fr.avenirsesr.portfolio.common.file.infrastructure.configuration.FileStorageConstants;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.infrastructure.fixture.FileFixture;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LocalFileClientTest {

  @Mock private FileResourceService fileResourceService;

  private LocalFileClient fileClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    FileStorageConstants.PHOTO_ENDPOINT_PREFIX = "/storage";
    fileClient = new LocalFileClient(fileResourceService);
  }

  @Test
  void shouldUploadFileAndReturnItsPublicRepresentation() throws IOException {
    BddLogger.given("an upload request holding the content of a file");
    byte[] content = "file content".getBytes(StandardCharsets.UTF_8);
    var request =
        new FileUploadRequest(
            "terms.pdf", "application/pdf", FileContent.ofBytes(content), content.length, false);
    File uploaded = FileFixture.create().toModel();
    when(fileResourceService.upload(anyString(), anyString(), anyLong(), any(), anyBoolean()))
        .thenReturn(uploaded);

    BddLogger.when("uploading it through the file client");
    FileDTO result = fileClient.upload(request);

    BddLogger.then("the file feature is asked to store the content and the file is returned");
    var fileNameCaptor = ArgumentCaptor.forClass(String.class);
    var mimeTypeCaptor = ArgumentCaptor.forClass(String.class);
    var sizeCaptor = ArgumentCaptor.forClass(Long.class);
    var contentCaptor = ArgumentCaptor.forClass(FileContent.class);
    var restrictedCaptor = ArgumentCaptor.forClass(Boolean.class);
    verify(fileResourceService)
        .upload(
            fileNameCaptor.capture(),
            mimeTypeCaptor.capture(),
            sizeCaptor.capture(),
            contentCaptor.capture(),
            restrictedCaptor.capture());

    assertThat(fileNameCaptor.getValue()).isEqualTo("terms.pdf");
    assertThat(mimeTypeCaptor.getValue()).isEqualTo("application/pdf");
    assertThat(sizeCaptor.getValue()).isEqualTo(content.length);
    assertThat(contentCaptor.getValue().open().readAllBytes()).isEqualTo(content);
    assertThat(restrictedCaptor.getValue()).isFalse();

    assertThat(result.id()).isEqualTo(uploaded.getId());
    assertThat(result.fileName()).isEqualTo(uploaded.getFileName());
    assertThat(result.fileSize()).isEqualTo(uploaded.getSize());
    assertThat(result.url()).isEqualTo("/storage/" + uploaded.getId());
  }

  @Test
  void shouldReturnFileMetadataWithoutItsContent() {
    BddLogger.given("a file stored by the file feature");
    File stored = FileFixture.create().toModel();
    when(fileResourceService.get(stored.getId())).thenReturn(stored);

    BddLogger.when("fetching it through the file client");
    FileDTO result = fileClient.get(stored.getId());

    BddLogger.then("its public representation is returned");
    assertThat(result.id()).isEqualTo(stored.getId());
    assertThat(result.fileType()).isEqualTo(stored.getFileType());
    assertThat(result.url()).isEqualTo("/storage/" + stored.getId());
  }

  @Test
  void shouldDelegateDeletionToTheFileFeature() {
    BddLogger.given("the id of a file to delete");
    UUID fileId = UUID.randomUUID();

    BddLogger.when("deleting it through the file client");
    fileClient.delete(fileId);

    BddLogger.then("the file feature is asked to delete it");
    verify(fileResourceService).delete(fileId);
  }
}
