package fr.avenirsesr.portfolio.student.association.domain.data;

import java.util.UUID;

public record AssociationSearchResultData(
    UUID id,
    String title,
    String category,
    boolean disabled,
    boolean limitReached,
    String description) {

  public AssociationSearchResultData(UUID id, String title, String category, boolean disabled) {
    this(id, title, category, disabled, false, null);
  }

  public AssociationSearchResultData(
      UUID id, String title, String category, boolean disabled, String description) {
    this(id, title, category, disabled, false, description);
  }
}
