package fr.avenirsesr.portfolio.user.domain.model.enums;

public enum EUserCategory {
  TEACHER("teacher"),
  STUDENT("student");

  private String name;

  EUserCategory(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static EUserCategory fromString(String name) {
    for (EUserCategory category : EUserCategory.values()) {
      if (category.name.equalsIgnoreCase(name)) {
        return category;
      }
    }
    return null;
  }
}
