package fr.avenirsesr.portfolio.ams.application.adapter.controller;

import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.ams.application.adapter.mapper.AmsViewMapper;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
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
  private final UserUtil userUtil;
  private final AMSService amsService;

  @GetMapping("/view")
  public ResponseEntity<PagedResponse<AmsViewDTO>> getAmsView(
      Principal principal,
      @RequestParam(value = "studentProgressId") UUID studentProgressId,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "pageSize", required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get AMS view for user [{}], studentProgressId [{}] with pagination"
            + " (page={}, pageSize={})",
        principal.getName(),
        studentProgressId,
        pageCriteria.page(),
        pageCriteria.pageSize());
    Student student = userUtil.getStudent(principal);

    PagedResult<AMS> pagedResult =
        amsService.findUserAmsByStudentProgress(student, studentProgressId, pageCriteria);

    List<AmsViewDTO> amsViewDTOs =
        pagedResult.content().stream().map(AmsViewMapper::toDto).toList();

    PagedResponse<AmsViewDTO> response =
        new PagedResponse<>(amsViewDTOs, PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(response);
  }
}
