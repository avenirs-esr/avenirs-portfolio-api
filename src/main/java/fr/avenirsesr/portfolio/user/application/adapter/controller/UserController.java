package fr.avenirsesr.portfolio.user.application.adapter.controller;

import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.application.adapter.mapper.ProfileOverviewMapper;
import fr.avenirsesr.portfolio.user.application.adapter.request.ProfileUpdateRequest;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.domain.utils.UserUtils;
import java.io.IOException;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/user")
public class UserController {

  private final UserService userService;
  private final UserUtil userUtil;

  @GetMapping("/{profile}/overview")
  public ResponseEntity<ProfileOverviewDTO> getProfile(
      Principal principal, @PathVariable String profile) {
    User user = userUtil.getUser(principal);
    EUserCategory userCategory = UserUtils.getUserCategory(profile);

    return switch (userCategory) {
      case STUDENT ->
          ResponseEntity.ok(ProfileOverviewMapper.userStudentDomainToDto(user.toStudent()));
      case TEACHER ->
          ResponseEntity.ok(ProfileOverviewMapper.userTeacherDomainToDto(user.toTeacher()));
    };
  }

  @PutMapping("/{profile}/update")
  public ResponseEntity<String> updateProfile(
      Principal principal,
      @PathVariable String profile,
      @RequestBody ProfileUpdateRequest request) {
    log.debug("Received request to update profile of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);

    userService.updateProfile(
        user,
        request.getFirstname(),
        request.getLastname(),
        request.getEmail(),
        request.getBio(),
        request.getProfilePicture(),
        request.getProfilePicture());
    return ResponseEntity.ok("Mise à jour faite.");
  }

  @PutMapping(value = "/{profile}/update/photo", consumes = "multipart/form-data")
  public ResponseEntity<String> updateProfilePhoto(
      Principal principal,
      @PathVariable String profile,
      @RequestParam("file") MultipartFile photoFile)
      throws IOException {
    log.debug("Received request to upload profile picture of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);
    EUserCategory userCategory = UserUtils.getUserCategory(profile);

    return switch (userCategory) {
      case STUDENT ->
          ResponseEntity.ok(userService.uploadProfilePicture(user.toStudent(), photoFile));
      case TEACHER ->
          ResponseEntity.ok(userService.uploadProfilePicture(user.toTeacher(), photoFile));
    };
  }

  @PutMapping(value = "/{profile}/update/cover", consumes = "multipart/form-data")
  public ResponseEntity<String> updateProfileCover(
      Principal principal,
      @PathVariable String profile,
      @RequestParam("file") MultipartFile coverFile)
      throws IOException {
    log.debug("Received request to upload cover picture of user [{}]", principal.getName());
    User user = userUtil.getUser(principal);
    EUserCategory userCategory = UserUtils.getUserCategory(profile);

    return switch (userCategory) {
      case STUDENT ->
          ResponseEntity.ok(userService.uploadCoverPicture(user.toStudent(), coverFile));
      case TEACHER ->
          ResponseEntity.ok(userService.uploadCoverPicture(user.toTeacher(), coverFile));
    };
  }
}
