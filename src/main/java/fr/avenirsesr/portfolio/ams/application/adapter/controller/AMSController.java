package fr.avenirsesr.portfolio.ams.application.adapter.controller;

import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.ams.application.adapter.mapper.AmsViewMapper;
import fr.avenirsesr.portfolio.ams.application.adapter.response.AmsViewResponse;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
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
        "Received request to get AMS view for user [{}], programProgressId [{}] with pagination"
            + " (page={}, pageSize={})",
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
