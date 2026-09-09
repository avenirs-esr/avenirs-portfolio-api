package fr.avenirsesr.portfolio.student.kit.application.adapter.controller;

import fr.avenirsesr.portfolio.student.kit.domain.port.input.KitService;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/kit")
public class KitController {
  private final KitService kitService;

  @PreAuthorize("hasAuthority('kit:download-media:own')")
  @GetMapping(value = "/download-media", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<StreamingResponseBody> downloadMedia(Principal principal) {
    log.debug("Received request to download kit media by user [{}]", principal.getName());
    StreamingResponseBody body = kitService::downloadMedia;
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"kit.zip\"")
        .body(body);
  }
}
