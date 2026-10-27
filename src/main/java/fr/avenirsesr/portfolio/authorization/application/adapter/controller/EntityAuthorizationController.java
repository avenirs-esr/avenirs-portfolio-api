package fr.avenirsesr.portfolio.authorization.application.adapter.controller;

import fr.avenirsesr.portfolio.authorization.application.adapter.dto.EntityAuthorizationsDTO;
import fr.avenirsesr.portfolio.authorization.application.adapter.dto.ListAccessDTO;
import fr.avenirsesr.portfolio.authorization.application.adapter.mapper.EntityAuthorizationDtoMapper;
import fr.avenirsesr.portfolio.authorization.application.adapter.request.EntityAuthorizationsRequest;
import fr.avenirsesr.portfolio.authorization.domain.port.input.EntityAuthorizationService;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/me/authorizations/{entityType}")
public class EntityAuthorizationController {
  private final EntityAuthorizationService entityAuthorizationService;
  private final EntityAuthorizationDtoMapper entityAuthorizationDtoMapper;

  @PostMapping("/query")
  public ResponseEntity<List<EntityAuthorizationsDTO>> getAuthorizations(
      @Parameter(
              name = "entityType",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EAuthorizationEntityType"))
          @PathVariable
          EAuthorizationEntityType entityType,
      @Valid @RequestBody EntityAuthorizationsRequest request) {
    var authorizations =
        entityAuthorizationDtoMapper.toDTOs(
            entityAuthorizationService.getAuthorizations(entityType, request.entityIds()));
    return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(authorizations);
  }

  @GetMapping("/list-access")
  public ResponseEntity<ListAccessDTO> getListAccess(
      @Parameter(
              name = "entityType",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EAuthorizationEntityType"))
          @PathVariable
          EAuthorizationEntityType entityType) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.noStore())
        .body(new ListAccessDTO(entityAuthorizationService.canAccessList(entityType)));
  }
}
