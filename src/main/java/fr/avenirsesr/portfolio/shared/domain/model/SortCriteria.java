package fr.avenirsesr.portfolio.shared.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ESortOrder;

public class SortCriteria {
  private final ESortField field;
  private final ESortOrder order;

  public SortCriteria(ESortField field, ESortOrder order) {
    this.field = field;
    this.order = order;
  }

  public ESortField getField() {
    return field;
  }

  public ESortOrder getOrder() {
    return order;
  }

  public static SortCriteria fromString(String input) {
    if (input == null || !input.contains(",")) {
      return null;
    }

    String[] parts = input.split(",");
    if (parts.length != 2) {
      return null;
    }

    try {
      ESortField field = ESortField.fromString(parts[0].trim());
      ESortOrder order = ESortOrder.fromString(parts[1].trim());

      return new SortCriteria(field, order);
    } catch (Exception exception) {
      return null;
    }
  }
}
