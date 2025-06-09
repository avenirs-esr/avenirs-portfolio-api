package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProfileOverviewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.request.ProfileUpdateRequest;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.input.UserService;
import fr.avenirsesr.portfolio.api.domain.utils.UserUtils;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/me/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/{profile}/overview")
  public ResponseEntity<ProfileOverviewDTO> getProfile(
      @RequestHeader("X-Signed-Context") String userIdRaw, @PathVariable String profile) {
    var userId = UUID.fromString(userIdRaw); // @Todo: fetch userLoggedIn

    EUserCategory userCategory = UserUtils.getUserCategory(profile);
    User user = userService.getProfile(userId);

    if (userCategory == EUserCategory.STUDENT) {
      return ResponseEntity.ok(ProfileOverviewMapper.userStudentDomainToDto(user.toStudent()));
    } else {
      return ResponseEntity.ok(ProfileOverviewMapper.userTeacherDomainToDto(user.toTeacher()));
    }
  }

  @PutMapping("/{profile}/update")
  public ResponseEntity<String> updateProfile(
      @RequestHeader("X-Signed-Context") String userIdRaw,
      @PathVariable String profile,
      @RequestBody ProfileUpdateRequest request) {
    var userId = UUID.fromString(userIdRaw); // @Todo: fetch userLoggedIn

    userService.updateProfile(
        userId,
        request.getFirstname(),
        request.getLastname(),
        request.getEmail(),
        request.getBio());
    return ResponseEntity.ok("Mise à jour faite.");
  }

  @PutMapping("/{profile}/update/photo")
  public ResponseEntity<String> updateProfilePhoto(
      @RequestHeader("X-Signed-Context") String userIdRaw,
      @PathVariable String profile,
      @RequestParam("file") MultipartFile photoFile)
      throws IOException {
    var userId = UUID.fromString(userIdRaw); // @Todo: fetch userLoggedIn

    userService.updateProfilePicture(userId, photoFile);
    return ResponseEntity.ok("Mise à jour faite.");
  }

  @PutMapping("/{profile}/update/cover")
  public ResponseEntity<String> updateProfileCover(
      @RequestHeader("X-Signed-Context") String userIdRaw,
      @PathVariable String profile,
      @RequestParam("file") MultipartFile coverFile)
      throws IOException {
    var userId = UUID.fromString(userIdRaw); // @Todo: fetch userLoggedIn

    userService.updateCoverPicture(userId, coverFile);
    return ResponseEntity.ok("Mise à jour faite.");
  }
}
