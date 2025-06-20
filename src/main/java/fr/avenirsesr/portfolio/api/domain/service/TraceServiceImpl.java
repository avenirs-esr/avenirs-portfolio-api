package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.api.domain.model.PageInfo;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.TraceConfigurationInfo;
import fr.avenirsesr.portfolio.api.domain.model.TraceView;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.api.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TraceServiceImpl implements TraceService {
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGESIZE = 8;
  private static final int MAX_TRACES_OVERVIEW = 3;
  private final TraceRepository traceRepository;
  private final ConfigurationService configurationService;

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
    return traceRepository.findLastsOf(user, MAX_TRACES_OVERVIEW);
  }

  @Override
  public TraceView getUnassociatedTraces(User user, Integer page, Integer pageSize) {
    page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
    pageSize = Optional.ofNullable(pageSize).orElse(DEFAULT_PAGESIZE);

    Page<TraceEntity> traceEntityPage =
        traceRepository.findAllUnassociatedPage(user, page, pageSize);

    List<Trace> traceList =
        traceEntityPage.getContent().stream().map(TraceMapper::toDomain).toList();
    int criticalCount = 0;

    TraceConfigurationInfo traceConfigurationInfo = configurationService.getTraceConfiguration();

    for (Trace trace : traceList) {
      if (hasEnoughDayUntilCritical(
          trace.getCreatedAt(),
          traceConfigurationInfo.maxDayRemaining()
              - traceConfigurationInfo.maxDayRemainingCritical())) {
        criticalCount++;
      }
    }

    return new TraceView(
        traceList,
        criticalCount,
        new PageInfo(
            traceEntityPage.getSize(),
            (int) traceEntityPage.getTotalElements(),
            traceEntityPage.getTotalPages(),
            traceEntityPage.getNumber()));
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

  public static boolean hasEnoughDayUntilCritical(
      Instant initialDate, int maxDaySinceCreationBeforeCritical) {
    return Duration.between(initialDate, Instant.now()).toDays()
        <= maxDaySinceCreationBeforeCritical;
  }
}
