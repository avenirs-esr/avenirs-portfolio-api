package fr.avenirsesr.portfolio.authorization.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.authorization.application.adapter.dto.EntityAuthorizationsDTO;
import fr.avenirsesr.portfolio.authorization.domain.data.EntityAuthorizationsData;
import fr.avenirsesr.portfolio.common.authorization.domain.model.enums.EAuthorization;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class EntityAuthorizationDtoMapperTest {
  private final EntityAuthorizationDtoMapper mapper =
      Mappers.getMapper(EntityAuthorizationDtoMapper.class);

  @Test
  void shouldSortTheAuthorizationsByDeclarationOrder() {
    BddLogger.given("an entity holding unsorted authorizations and another holding none");
    var entityId = UUID.randomUUID();
    var emptyEntityId = UUID.randomUUID();
    var data =
        List.of(
            new EntityAuthorizationsData(
                entityId,
                Set.of(EAuthorization.PUBLISH, EAuthorization.EDIT, EAuthorization.FEEDBACK)),
            new EntityAuthorizationsData(emptyEntityId, Set.of()));

    BddLogger.when("mapping them to DTOs");
    var dtos = mapper.toDTOs(data);

    BddLogger.then("the authorizations should be sorted and the order of the entities kept");
    assertThat(dtos)
        .containsExactly(
            new EntityAuthorizationsDTO(
                entityId,
                List.of(EAuthorization.EDIT, EAuthorization.FEEDBACK, EAuthorization.PUBLISH)),
            new EntityAuthorizationsDTO(emptyEntityId, List.of()));
  }
}
