package fr.avenirsesr.portfolio.file.application.adapter.controller;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.file.infrastructure.configuration.FileStorageConstants;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.io.IOException;
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
  private final UserResourceService userResourceService;
  private final FileStorageService fileStorageService;

  @GetMapping("/users/{fileId}")
  public ResponseEntity<ByteArrayResource> getUserResourceByFileId(@Valid @PathVariable UUID fileId)
      throws IOException {
    log.debug("Received request to get user photo id [{}]", fileId);
    var userPhoto = userResourceService.getUserPhotoById(fileId);
    byte[] photo = userResourceService.fetchContent(userPhoto);

    return ResponseEntity.ok()
        .contentType(MediaType.asMediaType(MimeType.valueOf(userPhoto.getFileType().getMimeType())))
        .body(new ByteArrayResource(photo));
  }

  @GetMapping("/users/default/{photoType}")
  public ResponseEntity<ByteArrayResource> getDefaultResource(
      @Valid
          @Parameter(
              name = "photoType",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/UserPhotoType"))
          @PathVariable
          EUserPhotoType photoType)
      throws IOException {
    log.debug("Received request to get default {} photo", photoType);
    byte[] photo =
        fileStorageService.get(
            switch (photoType) {
              case PROFILE -> FileStorageConstants.USER_PROFILE_DEFAULT_PATH;
              case COVER -> FileStorageConstants.USER_COVER_DEFAULT_PATH;
            });

    var fileType =
        switch (photoType) {
          case PROFILE -> FileStorageConstants.USER_PROFILE_DEFAULT_FILE_TYPE;
          case COVER -> FileStorageConstants.USER_COVER_DEFAULT_FILE_TYPE;
        };

    return ResponseEntity.ok()
        .contentType(MediaType.asMediaType(MimeType.valueOf(fileType.getMimeType())))
        .body(new ByteArrayResource(photo));
  }
}
