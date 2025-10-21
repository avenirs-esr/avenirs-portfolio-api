package fr.avenirsesr.portfolio.user.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.application.adapter.mapper.ProfileOverviewMapper;
import fr.avenirsesr.portfolio.user.application.adapter.request.ProfileUpdateRequest;
import fr.avenirsesr.portfolio.user.domain.data.UserPhotosData;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/users")
public class UserController {

  private final UserService userService;
  private final UserUtil userUtil;

  @GetMapping("/{userCategory}/overview")
  public ResponseEntity<ProfileOverviewDTO> getProfile(
      Principal principal,
      HttpServletRequest request,
      @Valid
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          @PathVariable
          EUserCategory userCategory) {
    UUID userId = UUID.fromString(principal.getName());

    UserProfileOverviewData overview = userService.getUserProfileOverviewDTO(userId, userCategory);
    var userPhotos = userService.getUserPhotos(userId, userCategory);
    String baseUrl = extractOrigin(request);

    return ResponseEntity.ok(
        ProfileOverviewMapper.userDomainToDto(
            overview,
            new UserPhotosData(
                userPhotos.profileFileId(),
                userPhotos.profileFileName(),
                baseUrl + userPhotos.profileFileUrl(),
                userPhotos.coverFileId(),
                userPhotos.coverFileName(),
                baseUrl + userPhotos.coverFileUrl())));
  }

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
    User user = userUtil.getUser(principal);

    userService.updateProfile(
        userCategory,
        user,
        request.getFirstname(),
        request.getLastname(),
        request.getEmail(),
        request.getBio());
    return ResponseEntity.ok("Mise à jour faite.");
  }

  private static String extractOrigin(HttpServletRequest request) {
    try {
      URI referer = URI.create(request.getHeader("Referer"));

      return ServletUriComponentsBuilder.fromRequestUri(request)
          .replacePath("/apim")
          .scheme(referer.getScheme() != null ? referer.getScheme() : request.getScheme())
          .host(referer.getHost() != null ? referer.getHost() : request.getServerName())
          .port(referer.getHost() != null ? referer.getPort() : request.getServerPort())
          .build()
          .toUriString();
    } catch (Exception e) {
      return null;
    }
  }
}
