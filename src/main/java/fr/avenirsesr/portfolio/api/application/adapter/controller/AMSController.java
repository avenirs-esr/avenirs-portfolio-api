package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.AmsViewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.response.AmsViewResponse;
import fr.avenirsesr.portfolio.api.application.adapter.util.UserUtil;
import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.port.input.AMSService;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
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
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGE_SIZE = 8;
  private static final int MAX_PAGE_SIZE = 12;
  private static final String DEFAULT_PAGE_STR = "0";
  private static final String DEFAULT_PAGE_SIZE_STR = "8";

  // TODO: use a service instead
  private final UserUtil userUtil;

  private final AMSService amsService;

  @GetMapping("/view")
  public ResponseEntity<AmsViewResponse> getAmsView(
      Principal principal,
      @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_STR) int page,
      @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE_STR) int size) {
    log.debug(
        "Received request to get AMS view for user [{}] with pagination (page={}, size={})",
        principal.getName(),
        page,
        size);

    page = page < 0 ? DEFAULT_PAGE : page;
    size = (size > 0 && size <= MAX_PAGE_SIZE) ? size : DEFAULT_PAGE_SIZE;

    Student student = userUtil.getStudent(principal);

    PagedResult<AMS> pagedResult = amsService.findUserAmsWithPagination(student, page, size);

    List<AmsViewDTO> amsViewDTOs =
        pagedResult.getContent().stream().map(AmsViewMapper::toDto).collect(Collectors.toList());

    PaginationInfo paginationInfo =
        new PaginationInfo(size, pagedResult.getTotalElements(), pagedResult.getTotalPages(), page);

    AmsViewResponse response = new AmsViewResponse(amsViewDTOs, paginationInfo);

    return ResponseEntity.ok(response);
  }
}
