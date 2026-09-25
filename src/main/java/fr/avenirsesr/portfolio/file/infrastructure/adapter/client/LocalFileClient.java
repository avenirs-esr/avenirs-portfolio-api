package fr.avenirsesr.portfolio.file.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.file.application.adapter.client.FileClient;
import fr.avenirsesr.portfolio.common.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.common.file.application.adapter.request.FileUploadRequest;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalFileClient implements FileClient {
  private final FileResourceService fileResourceService;
  private final FileDtoMapper fileDtoMapper;

  @Override
  public FileDTO upload(FileUploadRequest request) {
    log.debug("Uploading file {} of type {}", request.fileName(), request.mimeType());
    var file =
        fileResourceService.upload(
            request.fileName(),
            request.mimeType(),
            request.content().length,
            request.content(),
            request.isRestricted());
    return fileDtoMapper.fromDomain(file);
  }

  @Override
  public FileDTO get(UUID fileId) {
    log.debug("Fetching file {}", fileId);
    return fileDtoMapper.fromDomain(fileResourceService.get(fileId));
  }

  @Override
  public byte[] fetchContent(UUID fileId) {
    log.debug("Fetching content of file {}", fileId);
    return fileResourceService.fetchContent(fileId).content();
  }

  @Override
  public void delete(UUID fileId) {
    log.debug("Deleting file {}", fileId);
    fileResourceService.delete(fileId);
  }
}
