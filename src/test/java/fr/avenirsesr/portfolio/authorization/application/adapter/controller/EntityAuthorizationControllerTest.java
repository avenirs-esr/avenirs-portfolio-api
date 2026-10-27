package fr.avenirsesr.portfolio.authorization.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.authorization.application.adapter.dto.EntityAuthorizationsDTO;
import fr.avenirsesr.portfolio.authorization.application.adapter.mapper.EntityAuthorizationDtoMapper;
import fr.avenirsesr.portfolio.authorization.application.adapter.request.EntityAuthorizationsRequest;
import fr.avenirsesr.portfolio.authorization.domain.data.EntityAuthorizationsData;
import fr.avenirsesr.portfolio.authorization.domain.port.input.EntityAuthorizationService;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorizationEntityType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityAuthorizationControllerTest {

  @Mock private EntityAuthorizationService entityAuthorizationService;
  @Mock private EntityAuthorizationDtoMapper entityAuthorizationDtoMapper;

  @InjectMocks private EntityAuthorizationController controller;

  @Test
  void shouldReturnTheMappedAuthorizationsAndForbidCaching() {
    BddLogger.given("an entity holding authorizations");
    var data = new EntityAuthorizationsData(UUID.randomUUID(), Set.of(EAuthorization.EDIT));
    var dto = new EntityAuthorizationsDTO(data.entityId(), List.of(EAuthorization.EDIT));
    when(entityAuthorizationService.getAuthorizations(
            EAuthorizationEntityType.ACTIVITY, List.of(data.entityId())))
        .thenReturn(List.of(data));
    when(entityAuthorizationDtoMapper.toDTOs(List.of(data))).thenReturn(List.of(dto));

    BddLogger.when("querying the authorizations");
    var response =
        controller.getAuthorizations(
            EAuthorizationEntityType.ACTIVITY,
            new EntityAuthorizationsRequest(List.of(data.entityId())));

    BddLogger.then("the mapped authorizations should be returned, and caching forbidden");
    assertEquals(200, response.getStatusCode().value());
    assertEquals("no-store", response.getHeaders().getCacheControl());
    assertEquals(List.of(dto), response.getBody());
  }

  @Test
  void shouldReturnTheListAccessAndForbidCaching() {
    BddLogger.given("a user able to act on drafts");
    when(entityAuthorizationService.canAccessList(EAuthorizationEntityType.ACTIVITY_DRAFT))
        .thenReturn(true);

    BddLogger.when("checking the list access");
    var response = controller.getListAccess(EAuthorizationEntityType.ACTIVITY_DRAFT);

    BddLogger.then("the access should be granted and the response should forbid caching");
    assertNotNull(response.getBody());
    assertEquals(true, response.getBody().canAccessList());
    assertEquals("no-store", response.getHeaders().getCacheControl());
  }
}
