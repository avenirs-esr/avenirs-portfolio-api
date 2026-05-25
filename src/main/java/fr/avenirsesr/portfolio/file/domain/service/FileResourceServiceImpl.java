package fr.avenirsesr.portfolio.file.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.*;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.trace.domain.exception.InvalidTraceTypeException;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class FileResourceServiceImpl implements FileResourceService {
  private final FileStorageService fileStorageService;
  private final FileRepository fileRepository;
  private final TraceRepository traceRepository;
  private final LoggedInUserService loggedInUserService;
  private final TraceService traceService;
  private static final Map<EFileCategory, List<EFileType>> ALLOWED_FILE_TYPES_BY_CATEGORY =
      Map.of(EFileCategory.TRACE_ATTACHEMENT, Arrays.stream(EFileType.values()).toList());

  @Override
  public File upload(
      UUID elementId,
      EFileCategory fileCategory,
      String fileName,
      String mimeType,
      long size,
      byte[] content) {
    var loggedInUser = loggedInUserService.getLoggedInUser();
    switch (fileCategory) {
      case TRACE_ATTACHEMENT -> chekTraceAttachementUploadRG(loggedInUser, elementId);
    }

    var fileResource =
        new FileResource(
            UUID.randomUUID(), fileName, EFileType.fromMimeType(mimeType), size, content);

    if (!ALLOWED_FILE_TYPES_BY_CATEGORY.get(fileCategory).contains(fileResource.fileType())) {
      throw new FileTypeNotSupportedException();
    }
    if (fileResource.fileType().getSizeLimit().isLessThan(size)) {
      throw new FileSizeTooBigException();
    }

    var uri = fileStorageService.upload(fileResource);
    var allFiles = fileRepository.findAllByElement(elementId);
    var version = allFiles.stream().map(File::getVersion).max(Integer::compareTo).orElse(0) + 1;
    allFiles.forEach(a -> a.setActiveVersion(false));
    var file =
        File.create(
            fileResource.id(),
            elementId,
            fileCategory,
            fileResource.fileType(),
            fileResource.fileName(),
            fileResource.size(),
            version,
            true,
            uri,
            loggedInUser);
    var savedFiles =
        fileRepository.saveAll(Stream.concat(allFiles.stream(), Stream.of(file)).toList());

    switch (fileCategory) {
      case TRACE_ATTACHEMENT -> saveFileOnTrace(elementId, file);
    }

    return savedFiles.stream()
        .filter(f -> f.getId().equals(file.getId()))
        .findFirst()
        .orElseThrow();
  }

  private void saveFileOnTrace(UUID traceId, File file) {
    var trace = traceService.getTraceById(traceId);
    trace.setAttachment(file);
    traceRepository.save(trace);
  }

  private void chekTraceAttachementUploadRG(User loggedInUser, UUID traceId) {
    var trace = traceService.getTraceById(traceId);

    if (!trace.getUser().equals(loggedInUser)) {
      throw new UserNotAuthorizedException();
    }

    if (trace.getLink().isPresent()) {
      throw new InvalidTraceTypeException();
    }
  }

  @Override
  public File getFile(UUID fileId) {
    return null;
  }

  @Override
  public byte[] fetchContent(UUID fileId) {
    return new byte[0];
  }

  @Override
  public FileDownload download(UUID fileId) {
    var loggedInUser = loggedInUserService.getLoggedInUser();
    var file = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    switch (file.getFileCategory()) {
      case TRACE_ATTACHEMENT -> chekTraceAttachementDownloadRG(loggedInUser, file.getElementId());
    }
    return new FileDownload(file.getFileName(), fileStorageService.get(file.getUri()));
  }

  private void chekTraceAttachementDownloadRG(User loggedInUser, UUID traceId) {
    var trace = traceService.getTraceById(traceId);
    if (!trace.getUser().equals(loggedInUser)) {
      throw new UserNotAuthorizedException();
    }
  }
}
