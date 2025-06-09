package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TraceRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TraceServiceImpl implements TraceService {
  private static final int MAX_TRACKS_OVERVIEW = 3;
  private final TraceRepository traceRepository;

  @Override
  public String programNameOfTrace(Trace trace) {
    return trace.getSkillLevels().isEmpty()
            || trace.getSkillLevels().stream()
                .noneMatch(
                    skillLevel -> skillLevel.getSkill().getProgramProgress().getProgram().isAPC())
        ? EPortfolioType.LIFE_PROJECT.name()
        : trace.getSkillLevels().stream()
            .filter(skillLevel -> skillLevel.getSkill().getProgramProgress().getProgram().isAPC())
            .findAny()
            .orElseThrow()
            .getSkill()
            .getProgramProgress()
            .getProgram()
            .getName();
  }

  @Override
  public List<Trace> lastTracesOf(User user) {
    return traceRepository.findLastsOf(user, MAX_TRACKS_OVERVIEW);
  }
}
