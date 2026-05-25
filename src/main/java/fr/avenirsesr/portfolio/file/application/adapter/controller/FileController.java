package fr.avenirsesr.portfolio.file.application.adapter.controller;

import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.file.domain.exception.FileStorageException;
import fr.avenirsesr.portfolio.file.domain.model.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/storage")
public class FileController {
  private final FileResourceService fileResourceService;
  private final FileDtoMapper fileDtoMapper;

  @PostMapping(
      value = "/{fileCategory}/{elementId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileDTO> uploadFile(
      Principal principal,
      @Valid @PathVariable UUID elementId,
      @PathVariable
          @Parameter(
              name = "fileCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EFileCategory"))
          EFileCategory fileCategory,
      @RequestParam("file") MultipartFile file) {
    log.debug(
        "Received file upload request from user [{}] for element [{}] id : [{}]",
        principal.getName(),
        fileCategory,
        elementId);
    try {
      var createdFile =
          fileResourceService.upload(
              elementId,
              fileCategory,
              file.getOriginalFilename(),
              file.getContentType(),
              file.getSize(),
              file.getBytes());
      return ResponseEntity.status(HttpStatus.CREATED).body(fileDtoMapper.fromDomain(createdFile));
    } catch (IOException e) {
      throw new FileStorageException("Failed to read uploaded file", e);
    }
  }

  @GetMapping(value = "/{fileId}/download")
  public ResponseEntity<byte[]> downloadFile(
      Principal principal, @Valid @PathVariable UUID fileId) {
    log.debug(
        "Received file download request from user [{}] for file [{}]", principal.getName(), fileId);
    var downloadedFile = fileResourceService.download(fileId);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + downloadedFile.fileName() + "\"")
        .body(downloadedFile.content());
  }

  @DeleteMapping(path = "/{fileId}")
  public ResponseEntity<String> deleteFile(Principal principal, @Valid @PathVariable UUID fileId) {
    log.debug("Received request to delete file [{}] by user [{}]", fileId, principal.getName());

    fileResourceService.delete(fileId);

    return ResponseEntity.ok("Resource successfully deleted");
  }
}
