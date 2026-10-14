package fr.avenirsesr.portfolio.student.association.domain.data;

import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;

public record AssociationLimitData(
    EAssociationContextType associatedContextType, int max, long current) {
  public int remaining() {
    return (int) Math.max(0, max - current);
  }
}
