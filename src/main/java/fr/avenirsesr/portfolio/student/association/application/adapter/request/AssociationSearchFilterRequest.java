package fr.avenirsesr.portfolio.student.association.application.adapter.request;

import fr.avenirsesr.portfolio.student.association.domain.filter.AssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.filter.TraceAssociationSearchFilter;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;

public record AssociationSearchFilterRequest(Boolean isAssociated) {
  public AssociationSearchFilter toDomain(EAssociationContextType associatedContextType) {
    return switch (associatedContextType) {
      case TRACE -> new TraceAssociationSearchFilter(isAssociated);
      case DECLARED_ACTIVITY, DECLARED_SKILL, DECLARED_EXPERIENCE, DECLARED_PROGRAM ->
          AssociationSearchFilter.NONE;
    };
  }
}
