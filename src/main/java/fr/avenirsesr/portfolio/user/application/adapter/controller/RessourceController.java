package fr.avenirsesr.portfolio.user.application.adapter.controller;

import fr.avenirsesr.portfolio.shared.application.adapter.utils.RessourceUtils;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.user.domain.utils.UserUtils;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping
public class RessourceController {

  private final RessourceService ressourceService;

  @GetMapping("/photo/{profile}/{fileName}")
  public ResponseEntity<ByteArrayResource> getPhoto(
      @PathVariable String profile, @PathVariable String fileName) throws IOException {
    EUserCategory userCategory = UserUtils.getUserCategory(profile);
    byte[] photo = ressourceService.getPhoto(userCategory, fileName);
    ByteArrayResource resource = new ByteArrayResource(photo);

    return ResponseEntity.ok()
        .contentType(RessourceUtils.getImageExtensionMediaType(fileName))
        .body(resource);
  }

  @GetMapping("/cover/{profile}/{fileName}")
  public ResponseEntity<ByteArrayResource> getCover(
      @PathVariable String profile, @PathVariable String fileName) throws IOException {
    EUserCategory userCategory = UserUtils.getUserCategory(profile);
    byte[] photo = ressourceService.getCover(userCategory, fileName);
    ByteArrayResource resource = new ByteArrayResource(photo);

    return ResponseEntity.ok().contentType(getMediaType(fileName)).body(resource);
  }

  @GetMapping("/errors")
  public List<EErrorCode> getAllErrorCodes() {
    return List.of(EErrorCode.values());
  }

  private MediaType getMediaType(String fileName) {
    String extension = "";
    int index = fileName.lastIndexOf('.');

    if (index > 0 && index < fileName.length() - 1) {
      extension = fileName.substring(index + 1);
    }

    if (extension.equalsIgnoreCase("png")) {
      return MediaType.IMAGE_PNG;
    }

    return MediaType.IMAGE_JPEG;
  }
}
