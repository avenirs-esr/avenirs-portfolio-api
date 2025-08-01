package fr.avenirsesr.portfolio.additionalskill.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathSegments {
  private SegmentDetail skill;
  private SegmentDetail macroSkill;
  private SegmentDetail target;
  private SegmentDetail issue;
  private SegmentDetail domain;

  private PathSegments(
      SegmentDetail skill,
      SegmentDetail macroSkill,
      SegmentDetail target,
      SegmentDetail issue,
      SegmentDetail domain) {
    this.skill = skill;
    this.macroSkill = macroSkill;
    this.target = target;
    this.issue = issue;
    this.domain = domain;
  }

  public static PathSegments create(
      SegmentDetail skill,
      SegmentDetail macroSkill,
      SegmentDetail target,
      SegmentDetail issue,
      SegmentDetail domain) {
    return new PathSegments(skill, macroSkill, target, issue, domain);
  }

  public static PathSegments toDomain(
      SegmentDetail skill,
      SegmentDetail macroSkill,
      SegmentDetail target,
      SegmentDetail issue,
      SegmentDetail domain) {
    return new PathSegments(skill, macroSkill, target, issue, domain);
  }
}
