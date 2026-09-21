package fr.avenirsesr.portfolio.student.association.domain.filter;

public sealed interface AssociationSearchFilter
    permits AssociationSearchFilter.None, TraceAssociationSearchFilter {
  None NONE = new None();

  record None() implements AssociationSearchFilter {}
}
