package fr.avenirsesr.portfolio.file.application.adapter.controller;

import fr.avenirsesr.portfolio.file.application.adapter.dto.AttachmentUploadDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.AttachmentUploadDTOMapper;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.domain.model.User;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/storage/traces")
public class TraceAttachmentController {
  private final UserUtil userUtil;
  private final TraceAttachmentService service;

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
    User user = userUtil.getUser(principal);

    var attachment =
        service.uploadTraceAttachment(
            user.toStudent(),
            traceId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            file.getBytes());

    return ResponseEntity.status(201).body(AttachmentUploadDTOMapper.fromDomain(attachment));
  }
}
