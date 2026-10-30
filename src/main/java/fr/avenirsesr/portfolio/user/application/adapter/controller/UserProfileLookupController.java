package fr.avenirsesr.portfolio.user.application.adapter.controller;

import fr.avenirsesr.portfolio.user.application.adapter.dto.UserProfileDTO;
import fr.avenirsesr.portfolio.user.application.adapter.request.UserProfileLookupRequest;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileLookupData;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lets the back-office tell, for the users it administers, who already logged in to Cofolio and
 * which profile picture they uploaded. Reading the profile of somebody else is gated by the
 * permission to consult the users of one's institution.
 *
 * <p>The lookup is a POST although it reads: the eppns are sent in the body because a page of users
 * holds up to a hundred of them, which no longer fits comfortably in a query string.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserProfileLookupController {

  private final UserService userService;

  @PreAuthorize("hasAuthority('external-user:read')")
  @PostMapping("/profiles")
  public ResponseEntity<List<UserProfileDTO>> findProfilesByEppns(
      @RequestBody UserProfileLookupRequest request) {
    log.debug(
        "Looking up the profiles of {} eppn(s)",
        request.eppns() == null ? 0 : request.eppns().size());

    List<UserProfileDTO> profiles =
        userService.findProfilesByEppns(request.eppns()).stream()
            .map(UserProfileLookupController::toDto)
            .toList();

    return ResponseEntity.ok(profiles);
  }

  private static UserProfileDTO toDto(UserProfileLookupData profile) {
    return new UserProfileDTO(
        profile.eppn(),
        profile.hasConnected(),
        profile.userId(),
        profile.profilePictureId(),
        profile.staff(),
        profile.student());
  }
}
