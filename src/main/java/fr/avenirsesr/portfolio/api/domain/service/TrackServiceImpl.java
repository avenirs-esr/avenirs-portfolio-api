package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.input.TrackService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TrackRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TrackServiceImpl implements TrackService {
  private static final int MAX_TRACKS_OVERVIEW = 3;
  private final TrackRepository trackRepository;

  @Override
  public String programNameOfTrack(Track track) {
    return track.getSkillLevels().isEmpty()
            || track.getSkillLevels().stream()
                .noneMatch(
                    skillLevel -> skillLevel.getSkill().getProgramProgress().getProgram().isAPC())
        ? EPortfolioType.LIFE_PROJECT.name()
        : track.getSkillLevels().stream()
            .filter(skillLevel -> skillLevel.getSkill().getProgramProgress().getProgram().isAPC())
            .findAny()
            .orElseThrow()
            .getSkill()
            .getProgramProgress()
            .getProgram()
            .getName();
  }

  @Override
  public List<Track> lastTracksOf(User user) {
    return trackRepository.findLastsOf(user, MAX_TRACKS_OVERVIEW);
  }
}
