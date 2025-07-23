package fr.avenirsesr.portfolio.trace.domain.service;

import fr.avenirsesr.portfolio.configuration.domain.model.TraceConfigurationInfo;
import fr.avenirsesr.portfolio.configuration.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TraceView;
import fr.avenirsesr.portfolio.trace.domain.model.UnassociatedTracesSummary;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TraceServiceImpl implements TraceService {
  private static final int MAX_TRACES_OVERVIEW = 3;
  private final TraceRepository traceRepository;
  private final ConfigurationService configurationService;

  public static boolean isBelowThresholdDate(Instant initialDate, int maxDaySinceCreation) {
    return Duration.between(initialDate, Instant.now()).toDays() >= maxDaySinceCreation;
  }

  @Override
  public String programNameOfTrace(Trace trace) {
    // TODO: Refactor this method when skill levels are refactored
    return EPortfolioType.LIFE_PROJECT.name();
    //    return trace.getSkillLevels().isEmpty()
    //            || trace.getSkillLevels().stream()
    //                .noneMatch(
    //                    skillLevel ->
    // skillLevel.getSkill().getTrainingPath().getProgram().isAPC())
    //        ? EPortfolioType.LIFE_PROJECT.title()
    //        : trace.getSkillLevels().stream()
    //            .filter(skillLevel ->
    // skillLevel.getSkill().getTrainingPath().getProgram().isAPC())
    //            .findAny()
    //            .orElseThrow()
    //            .getSkill()
    //            .getTrainingPath()
    //            .getProgram()
    //            .getName();
  }

  @Override
  public List<Trace> lastTracesOf(User user) {
    return traceRepository.findLastsOf(user, MAX_TRACES_OVERVIEW);
  }

  @Override
  public TraceView getUnassociatedTraces(User user, PageCriteria pageCriteria) {

    PagedResult<Trace> pagedResult = traceRepository.findAllUnassociated(user, pageCriteria);

    int criticalCount = 0;

    TraceConfigurationInfo traceConfigurationInfo = configurationService.getTraceConfiguration();

    for (Trace trace : pagedResult.content()) {
      if (isBelowThresholdDate(
          trace.getCreatedAt(),
          traceConfigurationInfo.maxDayRemaining()
              - traceConfigurationInfo.maxDayRemainingCritical())) {
        criticalCount++;
      }
    }

    return new TraceView(pagedResult.content(), criticalCount, pagedResult.pageInfo());
  }

  @Override
  public void deleteById(User user, UUID id) {
    Optional<Trace> traceOptional = traceRepository.findById(id);

    if (traceOptional.isEmpty()) {
      throw new TraceNotFoundException();
    } else if (!traceOptional.get().getUser().getId().equals(user.getId())) {
      throw new UserNotAuthorizedException();
    }

    traceRepository.deleteById(id);
  }

  @Override
  public UnassociatedTracesSummary getUnassociatedTracesSummary(User user) {
    List<Trace> unassociatedTraces = traceRepository.findAllUnassociated(user);
    TraceConfigurationInfo traceConfigurationInfo = configurationService.getTraceConfiguration();

    int criticalCount = 0;
    int warningCount = 0;
    for (Trace trace : unassociatedTraces) {
      if (isBelowThresholdDate(
          trace.getCreatedAt(),
          traceConfigurationInfo.maxDayRemaining()
              - traceConfigurationInfo.maxDayRemainingCritical())) {
        criticalCount++;
      }
      if (isBelowThresholdDate(
          trace.getCreatedAt(),
          traceConfigurationInfo.maxDayRemaining()
              - traceConfigurationInfo.maxDayRemainingWarning())) {
        warningCount++;
      }
    }

    return new UnassociatedTracesSummary(unassociatedTraces.size(), warningCount, criticalCount);
  }

  @Override
  public void createTrace(
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String personalNote,
      String aiJustification) {
    var trace =
        Trace.create(
            UUID.randomUUID(), user, title, language, isGroup, aiJustification, personalNote);

    traceRepository.save(trace);
  }
}
