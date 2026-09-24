package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.readBytes;

import fr.avenirsesr.portfolio.common.file.application.adapter.client.FileClient;
import fr.avenirsesr.portfolio.common.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.common.file.application.adapter.request.FileUploadRequest;
import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/storage")
public class StorageController {
  private final FileResourceService fileResourceService;
  private final FileClient fileClient;
  private final ResourceLoader resourceLoader;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileDTO> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "isRestricted", defaultValue = "false") boolean isRestricted) {
    log.debug("Received request to upload file [{}]", file.getOriginalFilename());

    FileDTO uploaded =
        fileClient.upload(
            new FileUploadRequest(
                file.getOriginalFilename(), file.getContentType(), readBytes(file), isRestricted));

    return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
  }

  @GetMapping("/{fileId}")
  public ResponseEntity<ByteArrayResource> getResourceByFileId(@Valid @PathVariable UUID fileId) {
    log.debug("Received request to get user photo id [{}]", fileId);
    FileResource file = fileResourceService.fetchContent(fileId);

    return ResponseEntity.ok()
        .contentType(MediaType.asMediaType(MimeType.valueOf(file.fileType().getMimeType())))
        .body(new ByteArrayResource(file.content()));
  }

  @GetMapping("/default/cover-picture")
  public ResponseEntity<ByteArrayResource> getDefaultCoverPicture() {
    log.debug("Received request to get default cover photo");
    return serveDefaultPicture(
        FileStorageConstants.COVER_DEFAULT_PATH, FileStorageConstants.COVER_DEFAULT_FILE_TYPE);
  }

  @GetMapping("/default/profile-picture")
  public ResponseEntity<ByteArrayResource> getDefaultProfilePicture() {
    log.debug("Received request to get default profile photo");
    return serveDefaultPicture(
        FileStorageConstants.PROFILE_DEFAULT_PATH, FileStorageConstants.PROFILE_DEFAULT_FILE_TYPE);
  }

  /**
   * Serves a fallback picture from a Spring resource location rather than from the storage backend.
   * These pictures ship with the deployment and belong to no user, so making them travel through
   * the bucket would tie a static asset to the availability of the storage backend.
   */
  private ResponseEntity<ByteArrayResource> serveDefaultPicture(
      String location, EFileType fileType) {
    Resource resource = resourceLoader.getResource(location);

    if (!resource.exists()) {
      log.error("No default picture found at location {}", location);
      throw new FileNotFoundException();
    }

    try (InputStream content = resource.getInputStream()) {
      return ResponseEntity.ok()
          .contentType(MediaType.asMediaType(MimeType.valueOf(fileType.getMimeType())))
          .body(new ByteArrayResource(content.readAllBytes()));
    } catch (IOException e) {
      throw new FileStorageException("Failed to read default picture at location " + location, e);
    }
  }
}
