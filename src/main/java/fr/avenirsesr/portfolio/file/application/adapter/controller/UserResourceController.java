package fr.avenirsesr.portfolio.file.application.adapter.controller;

import fr.avenirsesr.portfolio.file.application.adapter.dto.UserPhotoUploadDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.UserPhotoUploadDTOMapper;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/storage/users")
public class UserResourceController {
  private final UserUtil userUtil;
  private final UserResourceService userResourceService;

  @PutMapping(value = "/{userCategory}/{photoType}", consumes = "multipart/form-data")
  public ResponseEntity<UserPhotoUploadDTO> updateProfilePhoto(
      Principal principal,
      @Valid @PathVariable EUserCategory userCategory,
      @Valid @PathVariable EUserPhotoType photoType,
      @RequestParam("file") MultipartFile file)
      throws IOException {
    log.debug("Received request to upload profile picture of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    var userPhoto =
        userResourceService.uploadPhoto(
            user,
            userCategory,
            photoType,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            file.getBytes());
    return ResponseEntity.ok(UserPhotoUploadDTOMapper.fromDomain(userPhoto));
  }
}
