package fr.avenirsesr.portfolio.student.association.application.adapter.request;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.filter.TraceAssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import org.junit.jupiter.api.Test;

class AssociationSearchFilterRequestTest {

  @Test
  void shouldBuildATraceFilterWhenSearchingTraces() {
    BddLogger.given("a search filter on the associated traces");
    var request = new AssociationSearchFilterRequest(true);

    BddLogger.when("building the filter of a trace search");
    var filter = request.toDomain(EAssociationContextType.TRACE);

    BddLogger.then("it should return a trace filter on the associated traces");
    assertThat(filter).isEqualTo(new TraceAssociationSearchFilter(true));
  }

  @Test
  void shouldBuildAnEmptyTraceFilterWhenSearchingTracesWithoutFilter() {
    BddLogger.given("a search filter without value");
    var request = new AssociationSearchFilterRequest(null);

    BddLogger.when("building the filter of a trace search");
    var filter = request.toDomain(EAssociationContextType.TRACE);

    BddLogger.then("it should return a trace filter without value");
    assertThat(filter).isEqualTo(new TraceAssociationSearchFilter(null));
  }

  @Test
  void shouldIgnoreTheTraceFilterWhenSearchingAnotherContext() {
    BddLogger.given("a search filter on the associated traces");
    var request = new AssociationSearchFilterRequest(true);

    BddLogger.when("building the filter of a search on the other contexts");

    BddLogger.then("it should return no filter");
    assertThat(request.toDomain(EAssociationContextType.DECLARED_ACTIVITY))
        .isEqualTo(AssociationSearchFilter.NONE);
    assertThat(request.toDomain(EAssociationContextType.DECLARED_SKILL))
        .isEqualTo(AssociationSearchFilter.NONE);
    assertThat(request.toDomain(EAssociationContextType.DECLARED_EXPERIENCE))
        .isEqualTo(AssociationSearchFilter.NONE);
  }
}
