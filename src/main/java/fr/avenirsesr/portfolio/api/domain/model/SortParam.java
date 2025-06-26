package fr.avenirsesr.portfolio.api.domain.model;

public class SortParam {
  private String field;
  private String direction;

  public SortParam(String sort) {
    if (sort != null) {
      String[] parts = sort.split(",");
      this.field = parts[0];
      this.direction = (parts.length > 1 && parts[1].equalsIgnoreCase("DESC")) ? "DESC" : "ASC";
    }
  }

  public SortParam(String field, String direction) {
    this.field = field;
    this.direction = direction;
  }

  public String getField() {
    return field;
  }

  public String getDirection() {
    return direction;
  }
}
