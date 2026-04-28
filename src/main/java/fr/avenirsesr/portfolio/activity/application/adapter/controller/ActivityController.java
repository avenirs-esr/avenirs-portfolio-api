package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.extractOrigin;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityDetailsDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityNavigationDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityDetailsDtoMapper;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityNavigationMapper;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityOverviewDtoMapper;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityDetailData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/activities")
public class ActivityController {
  private final ActivityService activityService;
  private final ActivityDetailsDtoMapper activityDetailsDtoMapper;
  private final ActivityNavigationMapper activityNavigationMapper;
  private final ActivityOverviewDtoMapper activityOverviewDtoMapper;

  @GetMapping("/{activityId}")
  public ResponseEntity<ActivityDetailsDTO> getActivityDetail(
      HttpServletRequest request, @PathVariable UUID activityId) {
    log.debug("Received request to get activity [{}] detail", activityId);

    ActivityDetailData activityDetail = activityService.getActivityDetail(activityId);
    String baseUrl = extractOrigin(request);
    return ResponseEntity.ok(activityDetailsDtoMapper.toDTO(activityDetail, baseUrl));
  }

  @GetMapping
  public ResponseEntity<PagedResponse<ActivityOverviewDTO>> getActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) EActivityThematic thematic) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to activities view of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        activityService.activitiesView(thematic, pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(activityOverviewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @GetMapping("/latest")
  public ResponseEntity<PagedResponse<ActivityOverviewDTO>> getLatestActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to latest activities view of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        activityService.latestActivitiesView(pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(activityOverviewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @GetMapping("/navigation")
  public ResponseEntity<List<ActivityNavigationDTO>> getActivityNavigation() {
    return ResponseEntity.ok(
        activityNavigationMapper.toDTO(activityService.getActivityNavigation()));
  }
}
