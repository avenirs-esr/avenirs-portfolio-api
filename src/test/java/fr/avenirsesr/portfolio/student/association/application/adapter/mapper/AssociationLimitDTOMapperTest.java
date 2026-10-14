package fr.avenirsesr.portfolio.student.association.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationLimitDTO;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationLimitData;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AssociationLimitDTOMapperTest {

  private final AssociationLimitDTOMapper mapper =
      Mappers.getMapper(AssociationLimitDTOMapper.class);

  @Test
  void shouldMapTheAssociatedContextTypeTheMaxAndTheRemainingAssociations() {
    BddLogger.given("a limit of 3 traces, 1 of them already associated");
    AssociationLimitData data = new AssociationLimitData(EAssociationContextType.TRACE, 3, 1);

    BddLogger.when("mapping to DTO");
    var dto = mapper.toDTO(data);

    BddLogger.then("it should return the context type, the max and the 2 remaining associations");
    assertEquals(EAssociationContextType.TRACE, dto.associatedContextType());
    assertEquals(3, dto.max());
    assertEquals(2, dto.remaining());
  }

  @Test
  void shouldReturnNoRemainingAssociationWhenTheLimitIsReached() {
    BddLogger.given("a limit of 3 traces, all of them already associated");
    AssociationLimitData data = new AssociationLimitData(EAssociationContextType.TRACE, 3, 3);

    BddLogger.when("mapping to DTO");
    var dto = mapper.toDTO(data);

    BddLogger.then("it should return 0 remaining associations");
    assertEquals(0, dto.remaining());
  }

  @Test
  void shouldMapEveryLimitInTheSameOrder() {
    BddLogger.given("the limits of two association types");
    List<AssociationLimitData> limits =
        List.of(
            new AssociationLimitData(EAssociationContextType.TRACE, 8, 6),
            new AssociationLimitData(EAssociationContextType.DECLARED_SKILL, 2, 2));

    BddLogger.when("mapping to DTOs");
    var dtos = mapper.toDTOs(limits);

    BddLogger.then("it should return a DTO per limit, in the same order");
    assertEquals(
        List.of(
            new AssociationLimitDTO(EAssociationContextType.TRACE, 8, 2),
            new AssociationLimitDTO(EAssociationContextType.DECLARED_SKILL, 2, 0)),
        dtos);
  }

  @Test
  void shouldMapNoLimitToNoDTO() {
    BddLogger.given("no limit");

    BddLogger.when("mapping to DTOs");
    var dtos = mapper.toDTOs(List.of());

    BddLogger.then("it should return no DTO");
    assertTrue(dtos.isEmpty());
  }
}
