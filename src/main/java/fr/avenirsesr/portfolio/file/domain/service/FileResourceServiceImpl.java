package fr.avenirsesr.portfolio.file.domain.service;

import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileSizeTooBigException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class FileResourceServiceImpl implements FileResourceService {
  private final FileStorageService fileStorageService;
  private final FileRepository fileRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public File upload(
      String fileName, String mimeType, long size, byte[] content, boolean isRestricted) {
    var loggedInUser = loggedInUserService.getLoggedInUser();

    var fileResource =
        new FileResource(
            UUID.randomUUID(), fileName, EFileType.fromMimeType(mimeType), size, content);

    if (fileResource.fileType().getSizeLimit().isLessThan(size)) {
      throw new FileSizeTooBigException();
    }

    var uri = fileStorageService.upload(fileResource);
    var file =
        File.create(
            fileResource.id(),
            fileResource.fileType(),
            fileResource.fileName(),
            fileResource.size(),
            uri,
            loggedInUser,
            isRestricted);
    return fileRepository.save(file);
  }

  @Override
  public File copy(UUID fileId) {
    var source = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    var content = fileStorageService.get(source.getUri());
    return upload(
        source.getFileName(),
        source.getFileType().getMimeType(),
        source.getSize(),
        content,
        source.isRestricted());
  }

  @Override
  public File get(UUID fileId) {
    return fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
  }

  @Override
  public FileResource fetchContent(UUID fileId) {
    var file = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    if (file.isRestricted()) {
      throw new FileNotFoundException();
    }
    byte[] content = fileStorageService.get(file.getUri());
    return new FileResource(
        file.getId(), file.getFileName(), file.getFileType(), file.getSize(), content);
  }

  @Override
  public FileDownload download(UUID fileId) {
    var file = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    return new FileDownload(file.getFileName(), fileStorageService.get(file.getUri()));
  }

  @Override
  public void delete(UUID fileId) {
    var file = fileRepository.findById(fileId).orElseThrow(FileNotFoundException::new);
    fileStorageService.delete(file.getUri());
    fileRepository.removeFromDatabase(file);
    log.info("File deleted: {}", file);
  }
}
