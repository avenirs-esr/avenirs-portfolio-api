package fr.avenirsesr.portfolio.selfknowledge.domain.model.enums;

public enum ESelfKnowledgeCategory {
  STRENGTHS(1, true),
  VALUES(2, true),
  ASPIRATIONS(3, true),
  MOTIVATION(4, false),
  IMPROVEMENT(5, false),
  INTERESTS(6, false),
  INSPIRATIONS(7, false),
  OBLIGATIONS(8, false),
  TESTIMONIALS(9, false);

  private final int order;
  private final boolean mandatory;

  ESelfKnowledgeCategory(int order, boolean mandatory) {
    this.order = order;
    this.mandatory = mandatory;
  }

  public int getOrder() {
    return order;
  }

  public boolean isMandatory() {
    return mandatory;
  }
}
