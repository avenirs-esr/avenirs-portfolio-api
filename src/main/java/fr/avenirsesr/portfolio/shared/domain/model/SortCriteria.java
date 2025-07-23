package fr.avenirsesr.portfolio.shared.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;

public record SortCriteria(ESortField field, ESortOrder order) {
  private static final SortCriteria DEFAULT_SORT =
      new SortCriteria(ESortField.NAME, ESortOrder.ASC);

  public static SortCriteria fromString(String input) {
    if (input == null || !input.contains(",")) {
      return DEFAULT_SORT;
    }

    String[] parts = input.split(",");
    if (parts.length != 2) {
      return DEFAULT_SORT;
    }

    try {
      ESortField field = ESortField.fromString(parts[0].trim());
      ESortOrder order = ESortOrder.fromString(parts[1].trim());

      return new SortCriteria(field, order);
    } catch (Exception exception) {
      return DEFAULT_SORT;
    }
  }
}
