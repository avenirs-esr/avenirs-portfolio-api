package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.AmsViewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.response.AmsViewResponse;
import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.model.PaginationInfo;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.AMSService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
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
  private static final String DEFAULT_PAGE = "0";
  private static final String DEFAULT_SIZE = "10";

  private final UserRepository userRepository;
  private final AMSService amsService;

  @GetMapping("/view")
  public ResponseEntity<AmsViewResponse> getAmsView(
      Principal principal,
      @RequestParam(value = "page", defaultValue = DEFAULT_PAGE) int page,
      @RequestParam(value = "size", defaultValue = DEFAULT_SIZE) int size) {
    log.debug(
        "Received request to get AMS view for user [{}] with pagination (page={}, size={})",
        principal.getName(),
        page,
        size);

    UUID userId = UUID.fromString(principal.getName());
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    if (!user.isStudent()) {
      log.error("User {} is not a student", principal.getName());
      throw new UserIsNotStudentException();
    }

    PagedResult<AMS> pagedResult = amsService.findUserAmsWithPagination(user, page, size);

    List<AmsViewDTO> amsViewDTOs =
        pagedResult.getContent().stream().map(AmsViewMapper::toDto).collect(Collectors.toList());

    PaginationInfo paginationInfo =
        new PaginationInfo(size, pagedResult.getTotalElements(), pagedResult.getTotalPages(), page);

    AmsViewResponse response = new AmsViewResponse(amsViewDTOs, paginationInfo);

    return ResponseEntity.ok(response);
  }
}
