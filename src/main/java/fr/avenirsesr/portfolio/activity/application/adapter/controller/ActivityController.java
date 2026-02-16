package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityOverviewDtoMapper;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/activities")
public class ActivityController {
  private final ActivityService activityService;

  @GetMapping
  public ResponseEntity<PagedResponse<ActivityOverviewDTO>> getActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) EActivityThematic thematic) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to activities view of user [{}] (page= {}, size= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        activityService.activitiesView(thematic, pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(ActivityOverviewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }
}
