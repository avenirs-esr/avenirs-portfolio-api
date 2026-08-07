package fr.avenirsesr.portfolio.user.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.extractOrigin;
import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.readBytes;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.HmacAuthenticationToken;
import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.user.application.adapter.dto.LoggedInUserDTO;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.application.adapter.dto.QuickLinksDTO;
import fr.avenirsesr.portfolio.user.application.adapter.mapper.ProfileOverviewMapper;
import fr.avenirsesr.portfolio.user.application.adapter.request.NotificationPreferencesRequest;
import fr.avenirsesr.portfolio.user.application.adapter.request.ProfileUpdateRequest;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me")
public class UserController {
  private final UserService userService;
  private final StudentService studentService;
  private final StaffService staffService;
  private final ProfileOverviewMapper profileOverviewMapper;
  private final FileDtoMapper fileDtoMapper;

  @GetMapping
  public ResponseEntity<LoggedInUserDTO> getMe(HmacAuthenticationToken authentication) {
    var me = userService.getMe();
    return ResponseEntity.ok(
        new LoggedInUserDTO(me.firstname(), me.lastname(), authentication.getRoles()));
  }

  @PreAuthorize("hasAuthority('profile:read:own')")
  @GetMapping("/{userCategory}/overview")
  public ResponseEntity<ProfileOverviewDTO> getProfile(
      HttpServletRequest request,
      @Valid
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          @PathVariable
          EUserCategory userCategory) {
    UserProfileOverviewData overview =
        switch (userCategory) {
          case STUDENT -> studentService.getStudentProfile();
          case STAFF -> staffService.getStaffProfile();
        };

    String baseUrl = extractOrigin(request);
    return ResponseEntity.ok(profileOverviewMapper.userDomainToDto(overview, baseUrl));
  }

  @PreAuthorize("hasAuthority('profile:read:own')")
  @GetMapping("/{userCategory}/quick-links")
  public ResponseEntity<QuickLinksDTO> getQuickLinks(
      @Valid
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          @PathVariable
          EUserCategory userCategory) {
    var data = userService.getQuickLinks(userCategory);
    return ResponseEntity.ok(
        new QuickLinksDTO(
            data.userId(),
            data.firstname(),
            data.lastname(),
            data.hasUnseenNotification(),
            data.unreadNotifications(),
            data.notificationEnabled()));
  }

  @PreAuthorize("hasAuthority('profile:update:own')")
  @PutMapping("/{userCategory}/update")
  public ResponseEntity<String> updateProfile(
      Principal principal,
      @Valid
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          @PathVariable
          EUserCategory userCategory,
      @RequestBody ProfileUpdateRequest request) {
    log.debug("Received request to update profile of user [{}]", principal.getName());
    userService.updateProfile(userCategory, request.getEmail(), request.getBio());
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAuthority('profile:update:own')")
  @PatchMapping("/preferences/notification")
  public ResponseEntity<String> updateNotificationPreferences(
      Principal principal, @RequestBody NotificationPreferencesRequest request) {
    log.debug(
        "Received request to update notification preferences of user [{}]", principal.getName());
    userService.updateNotificationPreferences(request.notificationEnabled());
    return ResponseEntity.noContent().build();
  }

  @PostMapping(
      value = "/{userCategory}/profile-picture",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAuthority('profile:update:own')")
  public ResponseEntity<FileDTO> uploadProfilePicture(
      Principal principal,
      @PathVariable
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          EUserCategory userCategory,
      @RequestParam("file") MultipartFile file) {
    log.debug("Received request to upload profile picture of user [{}]", principal.getName());
    var uploaded =
        userService.uploadProfilePicture(
            userCategory,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            readBytes(file));
    return ResponseEntity.status(HttpStatus.CREATED).body(fileDtoMapper.fromDomain(uploaded));
  }

  @PreAuthorize("hasAuthority('profile:update:own')")
  @DeleteMapping("/{userCategory}/profile-picture")
  public ResponseEntity<Void> deleteProfilePicture(
      Principal principal,
      @PathVariable
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          EUserCategory userCategory) {
    log.debug("Received request to delete profile picture of user [{}]", principal.getName());
    userService.deleteProfilePicture(userCategory);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(
      value = "/{userCategory}/cover-picture",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAuthority('profile:update:own')")
  public ResponseEntity<FileDTO> uploadCoverPicture(
      Principal principal,
      @PathVariable
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          EUserCategory userCategory,
      @RequestParam("file") MultipartFile file) {
    log.debug("Received request to upload cover picture of user [{}]", principal.getName());
    var uploaded =
        userService.uploadCoverPicture(
            userCategory,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            readBytes(file));
    return ResponseEntity.status(HttpStatus.CREATED).body(fileDtoMapper.fromDomain(uploaded));
  }

  @PreAuthorize("hasAuthority('profile:update:own')")
  @DeleteMapping("/{userCategory}/cover-picture")
  public ResponseEntity<Void> deleteCoverPicture(
      Principal principal,
      @PathVariable
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          EUserCategory userCategory) {
    log.debug("Received request to delete cover picture of user [{}]", principal.getName());
    userService.deleteCoverPicture(userCategory);
    return ResponseEntity.noContent().build();
  }
}
