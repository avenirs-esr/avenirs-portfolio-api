package fr.avenirsesr.portfolio.file.domain.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.shared.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TraceAttachmentServiceImpl implements TraceAttachmentService {
  private final TraceAttachmentRepository traceAttachmentRepository;
  private final TraceRepository traceRepository;
  private final FileStorageService fileStorageService;
  private final TraceService traceService;

  @Override
  public TraceAttachment uploadTraceAttachment(
      Student student, UUID traceId, String fileName, String mimeType, long size, byte[] content)
      throws IOException {
    var trace = traceRepository.findById(traceId).orElseThrow(TraceNotFoundException::new);
    var allTraceAttachments = traceAttachmentRepository.findByTrace(trace);

    if (!trace.getUser().equals(student.getUser())) {
      throw new UserNotAuthorizedException();
    }

    try {
      var fileType = EFileType.fromMimeType(mimeType);
      if (fileType.getSizeLimit().isLessThan(size)) {
        throw new FileSizeTooBigException();
      }

      var fileResource = new FileResource(UUID.randomUUID(), fileName, fileType, size, content);
      var uri = fileStorageService.upload(fileResource);

      var newAttachment = createAttachment(student, allTraceAttachments, fileResource, trace, uri);

      traceAttachmentRepository.saveAll(
          Stream.concat(allTraceAttachments.stream(), Stream.of(newAttachment)).toList());
      log.info("New trace attachment saved: {}", newAttachment);
      return newAttachment;
    } catch (Exception e) {
      log.error("Failed to upload trace attachment for trace {}", trace, e);
      if (allTraceAttachments.isEmpty()) {
        traceService.deleteById(student.getUser(), trace.getId());
      }
      throw e;
    }
  }

  private static TraceAttachment createAttachment(
      Student student,
      List<TraceAttachment> allTraceAttachments,
      FileResource fileResource,
      Trace trace,
      String uri) {
    var version =
        allTraceAttachments.stream()
                .map(TraceAttachment::getVersion)
                .max(Integer::compareTo)
                .orElse(0)
            + 1;
    allTraceAttachments.forEach(a -> a.setActiveVersion(false));

    return TraceAttachment.create(
        fileResource.id(),
        trace,
        fileResource.fileName(),
        fileResource.fileType(),
        fileResource.size(),
        version,
        true,
        uri,
        student.getUser());
  }
}
