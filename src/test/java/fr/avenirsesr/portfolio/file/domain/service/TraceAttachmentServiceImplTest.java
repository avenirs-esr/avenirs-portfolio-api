package fr.avenirsesr.portfolio.file.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStoragePort;
import fr.avenirsesr.portfolio.file.infrastructure.fixture.TraceAttachmentFixture;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class TraceAttachmentServiceImplTest {

  @Mock private TraceAttachmentRepository traceAttachmentRepository;
  @Mock private FileStoragePort fileStoragePort;
  @Mock private TraceRepository traceRepository;
  @Mock private TraceService traceService;

  @InjectMocks private TraceAttachmentServiceImpl service;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void uploadTraceAttachment_shouldSaveNewAttachmentAndReturnIt() throws IOException {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

    when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

    // FileResource real instance
    when(fileStoragePort.upload(any(FileResource.class))).thenReturn("uri/to/file.txt");

    // Existing attachments with versions 1 and 2
    TraceAttachment existing1 =
        TraceAttachmentFixture.create().withTrace(trace).withActiveVersion(false).toModel();
    TraceAttachment existing2 =
        TraceAttachmentFixture.create().withTrace(trace).withVersion(2).toModel();
    when(traceAttachmentRepository.findByTrace(trace)).thenReturn(List.of(existing1, existing2));

    ArgumentCaptor<List<TraceAttachment>> captor = ArgumentCaptor.forClass(List.class);

    TraceAttachment result =
        service.uploadTraceAttachment(
            student, traceId, "file.txt", EFileType.TXT.getMimeType(), 1234L, "data".getBytes());

    verify(traceAttachmentRepository).findByTrace(trace);
    verify(traceAttachmentRepository).saveAll(captor.capture());

    List<TraceAttachment> savedAttachments = captor.getValue();
    assertThat(savedAttachments).hasSize(3);

    TraceAttachment newAttachment = savedAttachments.get(savedAttachments.size() - 1);
    assertThat(newAttachment.getVersion()).isEqualTo(3);
    assertThat(newAttachment.isActiveVersion()).isTrue();
    assertThat(newAttachment.getUri()).isEqualTo("uri/to/file.txt");

    assertThat(result).isEqualTo(newAttachment);

    // Old attachments' activeVersion set to false
    assertThat(savedAttachments.get(0).isActiveVersion()).isFalse();
    assertThat(savedAttachments.get(1).isActiveVersion()).isFalse();
  }

  @Test
  void uploadTraceAttachment_noExistingAttachments_shouldVersionOne() throws IOException {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

    when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

    when(fileStoragePort.upload(any(FileResource.class))).thenReturn("some-uri");

    when(traceAttachmentRepository.findByTrace(trace)).thenReturn(List.of());

    TraceAttachment result =
        service.uploadTraceAttachment(
            student, traceId, "file.txt", EFileType.TXT.getMimeType(), 1234L, "data".getBytes());

    assertThat(result.getVersion()).isEqualTo(1);
    assertThat(result.isActiveVersion()).isTrue();

    verify(traceAttachmentRepository).saveAll(anyList());
  }

  @Test
  void uploadTraceAttachment_shouldPropagateIOException() throws IOException {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

    when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

    when(fileStoragePort.upload(any(FileResource.class)))
        .thenThrow(new IOException("Disk write error"));

    IOException thrown =
        catchThrowableOfType(
            () ->
                service.uploadTraceAttachment(
                    student,
                    traceId,
                    "file.txt",
                    EFileType.TXT.getMimeType(),
                    1234L,
                    "data".getBytes()),
            IOException.class);

    assertThat(thrown).isNotNull();
    assertThat(thrown).hasMessageContaining("Disk write error");

    verify(traceAttachmentRepository, never()).saveAll(any());
  }

  @Test
  void uploadTraceAttachment_shouldThrowFileSizeTooBigException_andDeleteTraceWhenNoAttachments() {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();
    Trace trace = TraceFixture.create().withId(traceId).withUser(student.getUser()).toModel();

    when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
    when(traceAttachmentRepository.findByTrace(trace)).thenReturn(List.of());

    FileSizeTooBigException thrown =
        catchThrowableOfType(
            () ->
                service.uploadTraceAttachment(
                    student,
                    traceId,
                    "bigfile.txt",
                    EFileType.TXT.getMimeType(),
                    10_000_000L,
                    "data".getBytes()),
            FileSizeTooBigException.class);

    assertThat(thrown).isNotNull();
    verify(traceService).deleteById(student.getUser(), traceId);
  }

  @Test
  void
      uploadTraceAttachment_shouldThrowFileTypeNotSupportedException_andDeleteTraceWhenNoAttachments()
          throws IOException {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();
    Trace trace = TraceFixture.create().withId(traceId).withUser(student.getUser()).toModel();

    when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
    when(traceAttachmentRepository.findByTrace(trace)).thenReturn(List.of());

    when(fileStoragePort.upload(any(FileResource.class)))
        .thenThrow(new FileTypeNotSupportedException("File type not supported"));

    FileTypeNotSupportedException thrown =
        catchThrowableOfType(
            () ->
                service.uploadTraceAttachment(
                    student,
                    traceId,
                    "file.unsupported",
                    "unsupported-mimetype",
                    1234L,
                    "data".getBytes()),
            FileTypeNotSupportedException.class);

    assertThat(thrown).isNotNull();
    verify(traceService).deleteById(student.getUser(), traceId);
  }

  @Test
  void uploadTraceAttachment_shouldPropagateIOException_andDeleteTraceWhenNoAttachments()
      throws IOException {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();
    Trace trace = TraceFixture.create().withId(traceId).withUser(student.getUser()).toModel();

    when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
    when(traceAttachmentRepository.findByTrace(trace)).thenReturn(List.of());

    when(fileStoragePort.upload(any(FileResource.class)))
        .thenThrow(new IOException("Disk write error"));

    IOException thrown =
        catchThrowableOfType(
            () ->
                service.uploadTraceAttachment(
                    student,
                    traceId,
                    "file.txt",
                    EFileType.TXT.getMimeType(),
                    1234L,
                    "data".getBytes()),
            IOException.class);

    assertThat(thrown).isNotNull();
    assertThat(thrown).hasMessageContaining("Disk write error");

    verify(traceService).deleteById(student.getUser(), traceId);
    verify(traceAttachmentRepository, never()).saveAll(any());
  }

  @Test
  void uploadTraceAttachment_shouldThrowTraceNotFoundException() {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();

    when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                service.uploadTraceAttachment(
                    student,
                    traceId,
                    "file.txt",
                    EFileType.TXT.getMimeType(),
                    1234L,
                    "data".getBytes()))
        .isInstanceOf(fr.avenirsesr.portfolio.file.domain.exception.TraceNotFoundException.class);
  }

  @Test
  void uploadTraceAttachment_shouldThrowUserNotAuthorizedException() {
    Student student = UserFixture.createStudent().toModel().toStudent();
    UUID traceId = UUID.randomUUID();
    // Trace owned by a different user
    Trace trace = TraceFixture.create().toModel();

    when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

    assertThatThrownBy(
            () ->
                service.uploadTraceAttachment(
                    student,
                    traceId,
                    "file.txt",
                    EFileType.TXT.getMimeType(),
                    1234L,
                    "data".getBytes()))
        .isInstanceOf(
            fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException.class);
  }
}
