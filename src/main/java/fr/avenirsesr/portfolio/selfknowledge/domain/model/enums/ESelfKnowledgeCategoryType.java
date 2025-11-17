package fr.avenirsesr.portfolio.selfknowledge.domain.model.enums;

public enum ESelfKnowledgeCategoryType {
  STRENGTHS(1),
  VALUES(2),
  ASPIRATIONS(3),
  MOTIVATION(4),
  IMPROVEMENT(5),
  INTERESTS(6),
  INSPIRATIONS(7),
  OBLIGATIONS(8),
  TESTIMONIALS(9);

  private final int order;

  ESelfKnowledgeCategoryType(int order) {
    this.order = order;
  }

  public int getOrder() {
    return order;
  }
}
