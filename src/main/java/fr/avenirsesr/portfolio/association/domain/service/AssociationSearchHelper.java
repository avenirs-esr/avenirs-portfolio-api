package fr.avenirsesr.portfolio.association.domain.service;

import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssociationSearchHelper {
  private final AssociationService associationService;

  /**
   * Shared model for searching for entities that are candidates for association
   *
   * @param sourceEntityId ID of the source entity (e.g., traceId, declaredActivityId)
   * @param sourceEntityClass class of the source entity (e.g., Trace.class)
   * @param associationType type of association being searched for
   * @param associationIdExtractor extractor for the relevant ID from the association (getId1 or
   *     getId2)
   * @param searchResults paginated results of the search for candidate entities
   * @param resultIdExtractor extractor for the ID from the search result
   * @param resultTitleExtractor extractor for the title from the search result
   * @param resultCategoryExtractor extractor for the category (thematic, type, etc.) or null
   * @param additionalDisabledCondition additional condition for disabling (e.g., activity
   *     completed)
   * @return paginated results with the disabled flag calculated
   */
  public <T> PagedResult<AssociationSearchResultData> searchForAssociation(
      UUID sourceEntityId,
      Class<?> sourceEntityClass,
      EAssociationType associationType,
      Function<Association, UUID> associationIdExtractor,
      PagedResult<T> searchResults,
      Function<T, UUID> resultIdExtractor,
      Function<T, String> resultTitleExtractor,
      Function<T, String> resultCategoryExtractor,
      Predicate<T> additionalDisabledCondition) {

    Set<UUID> alreadyAssociatedIds =
        sourceEntityId != null
            ? associationService
                .getAllOf(sourceEntityId, sourceEntityClass, List.of(associationType))
                .stream()
                .map(associationIdExtractor)
                .collect(Collectors.toSet())
            : Set.of();

    var mappedContent =
        searchResults.content().stream()
            .map(
                item ->
                    new AssociationSearchResultData(
                        resultIdExtractor.apply(item),
                        resultTitleExtractor.apply(item),
                        resultCategoryExtractor != null
                            ? resultCategoryExtractor.apply(item)
                            : null,
                        alreadyAssociatedIds.contains(resultIdExtractor.apply(item))
                            || additionalDisabledCondition.test(item)))
            .toList();

    return new PagedResult<>(mappedContent, searchResults.pageInfo());
  }
}
