package fr.avenirsesr.portfolio.authorization.application.adapter.controller;

import fr.avenirsesr.portfolio.authorization.application.adapter.dto.UserEntityAuthorizationsDTO;
import fr.avenirsesr.portfolio.authorization.application.adapter.mapper.UserEntityAuthorizationDtoMapper;
import fr.avenirsesr.portfolio.authorization.domain.data.UserEntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.port.input.UserAuthorizationQueryService;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/internal/users/{eppn}/authorizations")
public class InternalUserAuthorizationController {
  private final UserAuthorizationQueryService userAuthorizationQueryService;
  private final UserEntityAuthorizationDtoMapper userEntityAuthorizationDtoMapper;

  @PreAuthorize("hasAuthority('external-user:read')")
  @GetMapping("/types")
  public ResponseEntity<List<EAuthorizationEntityType>> getAvailableTypes(
      @PathVariable String eppn, @RequestParam Set<UUID> institutionIds) {
    log.debug("Getting the authorization types of {} within {}", eppn, institutionIds);

    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .body(userAuthorizationQueryService.getAvailableTypes(eppn, institutionIds));
  }

  @PreAuthorize("hasAuthority('external-user:read')")
  @GetMapping
  public ResponseEntity<PagedResponse<UserEntityAuthorizationsDTO>> getAuthorizations(
      @PathVariable String eppn,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAuthorizationEntityType"))
          @RequestParam
          EAuthorizationEntityType entityType,
      @RequestParam Set<UUID> institutionIds,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    PageCriteria pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Getting the {} authorizations of {} within {} {}",
        entityType,
        eppn,
        institutionIds,
        pageCriteria);

    PagedResult<UserEntityAuthorizationsData> result =
        userAuthorizationQueryService.getAuthorizations(
            eppn, entityType, institutionIds, pageCriteria);

    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .body(
            new PagedResponse<>(
                userEntityAuthorizationDtoMapper.toDTOs(result.content()),
                PageInfoDTO.fromDomain(result.pageInfo())));
  }
}
