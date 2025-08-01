package fr.avenirsesr.portfolio.user.application.adapter.controller;

import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.application.adapter.mapper.ProfileOverviewMapper;
import fr.avenirsesr.portfolio.user.application.adapter.request.ProfileUpdateRequest;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
      @Valid
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/UserCategory"))
          @PathVariable
          EUserCategory userCategory) {
    User user = userUtil.getUser(principal);
    var userPhotos = userService.getUserPhotos(user.getId(), userCategory);

    return ResponseEntity.ok(ProfileOverviewMapper.userDomainToDto(user, userCategory, userPhotos));
  }

  @PutMapping("/{userCategory}/update")
  public ResponseEntity<String> update(
      Principal principal,
      @Valid
          @Parameter(
              name = "userCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/UserCategory"))
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
}
