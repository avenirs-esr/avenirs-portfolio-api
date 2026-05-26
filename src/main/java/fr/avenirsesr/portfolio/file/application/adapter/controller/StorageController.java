package fr.avenirsesr.portfolio.file.application.adapter.controller;

import fr.avenirsesr.portfolio.file.domain.model.FileResource;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/storage")
public class StorageController {
  private final FileResourceService fileResourceService;
  private final FileStorageService fileStorageService;

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
    byte[] photo = fileStorageService.get(FileStorageConstants.COVER_DEFAULT_PATH);

    return ResponseEntity.ok()
        .contentType(
            MediaType.asMediaType(
                MimeType.valueOf(FileStorageConstants.COVER_DEFAULT_FILE_TYPE.getMimeType())))
        .body(new ByteArrayResource(photo));
  }

  @GetMapping("/default/profile-picture")
  public ResponseEntity<ByteArrayResource> getDefaultProfilePicture() {
    log.debug("Received request to get default profile photo");
    byte[] photo = fileStorageService.get(FileStorageConstants.PROFILE_DEFAULT_PATH);

    return ResponseEntity.ok()
        .contentType(
            MediaType.asMediaType(
                MimeType.valueOf(FileStorageConstants.PROFILE_DEFAULT_FILE_TYPE.getMimeType())))
        .body(new ByteArrayResource(photo));
  }
}
