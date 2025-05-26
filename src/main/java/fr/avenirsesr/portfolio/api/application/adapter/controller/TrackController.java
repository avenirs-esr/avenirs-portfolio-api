package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.TrackOverviewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.TrackOverviewMapper;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.TrackService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/track")
public class TrackController {
  private final UserRepository userRepository;
  private final TrackService trackService;

  @GetMapping("/overview")
  public ResponseEntity<List<TrackOverviewDTO>> getTrackOverview(
      @RequestHeader("X-Signed-Context") String userIdRaw) {
    log.info("Received request to track overview of user [{}]", userIdRaw);
    UUID userId = UUID.fromString(userIdRaw); // @Todo: fetch userLoggedIn
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    List<Track> tracks = trackService.lastTracksOf(user);

    List<TrackOverviewDTO> response =
        tracks.stream()
            .map(track -> TrackOverviewMapper.toDTO(track, trackService.programNameOfTrack(track)))
            .toList();

    return ResponseEntity.ok(response);
  }
}
