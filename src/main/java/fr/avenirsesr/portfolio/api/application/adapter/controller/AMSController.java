package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.AmsViewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.response.AmsViewResponse;
import fr.avenirsesr.portfolio.api.application.adapter.util.UserUtil;
import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.port.input.AMSService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
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
@RequestMapping("/me/ams")
public class AMSController {

  // TODO: use a service instead
  private final UserUtil userUtil;

  private final AMSService amsService;

  @GetMapping("/view")
  public ResponseEntity<AmsViewResponse> getAmsView(
      Principal principal,
      @RequestParam(value = "programProgressId") UUID programProgressId,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "pageSize", required = false) Integer pageSize) {
    log.debug(
        "Received request to get AMS view for user [{}], programProgressId [{}] with pagination (page={}, pageSize={})",
        principal.getName(),
        programProgressId,
        page,
        pageSize);
    Student student = userUtil.getStudent(principal);

    PagedResult<AMS> pagedResult =
        amsService.findUserAmsByProgramProgressWithPagination(
            student, programProgressId, page, pageSize);

    List<AmsViewDTO> amsViewDTOs =
        pagedResult.content().stream().map(AmsViewMapper::toDto).toList();

    PageInfo pageInfo =
        new PageInfo(
            pagedResult.pageSize(),
            pagedResult.totalElements(),
            pagedResult.totalPages(),
            pagedResult.page());

    AmsViewResponse response = new AmsViewResponse(amsViewDTOs, pageInfo);

    return ResponseEntity.ok(response);
  }
}
