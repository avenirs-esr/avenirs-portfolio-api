package fr.avenirsesr.portfolio.additional.skill.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathSegments {
  private SegmentDetail issue;
  private SegmentDetail target;
  private SegmentDetail macroSkill;
  private SegmentDetail skill;

  private PathSegments(
      SegmentDetail issue, SegmentDetail target, SegmentDetail macroSkill, SegmentDetail skill) {
    this.issue = issue;
    this.target = target;
    this.macroSkill = macroSkill;
    this.skill = skill;
  }

  public static PathSegments create(
      SegmentDetail issue, SegmentDetail target, SegmentDetail macroSkill, SegmentDetail skill) {
    return new PathSegments(issue, target, macroSkill, skill);
  }
}
