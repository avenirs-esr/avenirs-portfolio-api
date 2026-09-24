package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static fr.avenirsesr.portfolio.common.file.application.adapter.MultipartFileReader.readBytes;

import fr.avenirsesr.portfolio.common.file.application.adapter.client.FileClient;
import fr.avenirsesr.portfolio.common.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.common.file.application.adapter.request.FileUploadRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/files")
public class FileController {
  private final FileClient fileClient;

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
}
