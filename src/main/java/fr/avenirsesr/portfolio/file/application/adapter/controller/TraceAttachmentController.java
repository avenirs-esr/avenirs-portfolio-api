package fr.avenirsesr.portfolio.file.application.adapter.controller;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.AttachmentUploadDTOMapper;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/me/storage/traces")
public class TraceAttachmentController {
  private final TraceAttachmentService service;
  private final AttachmentUploadDTOMapper attachmentUploadDTOMapper;

  @PostMapping(value = "/{traceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AttachmentUploadDTO> uploadAttachment(
      Principal principal,
      @Valid @PathVariable UUID traceId,
      @RequestParam("file") MultipartFile file)
      throws IOException {
    log.debug(
        "Received attachment upload request from user [{}] for trace [{}]",
        principal.getName(),
        traceId);
    var attachment =
        service.uploadTraceAttachment(
            traceId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            file.getBytes());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(attachmentUploadDTOMapper.fromDomain(attachment));
  }

  @GetMapping("/attachments/{attachmentId}")
  @Operation(
      summary = "Download an attachment",
      description =
          "Retrieves the binary content of a specific attachment associated with a trace.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "File downloaded successfully",
            content =
                @Content(
                    mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "404", description = "Attachment not found")
      })
  public ResponseEntity<byte[]> downloadAttachment(@Valid @PathVariable UUID attachmentId)
      throws IOException {
    log.debug("Received attachment download request for attachment [{}]", attachmentId);
    var attachment = service.downloadTraceAttachment(attachmentId);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + attachment.fileName() + "\"")
        .body(attachment.content());
  }
}
