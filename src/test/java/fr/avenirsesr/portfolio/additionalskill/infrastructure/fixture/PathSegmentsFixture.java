package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;

public class PathSegmentsFixture {
  private static final FakerProvider faker = new FakerProvider();

  private SegmentDetail issue;
  private SegmentDetail target;
  private SegmentDetail macroSkill;
  private SegmentDetail skill;

  private PathSegmentsFixture() {
    this.issue = SegmentDetailFixture.create().toModel();
    this.target = SegmentDetailFixture.create().toModel();
    this.macroSkill = SegmentDetailFixture.create().toModel();
    this.skill = SegmentDetailFixture.create().toModel();
  }

  public static PathSegmentsFixture create() {
    return new PathSegmentsFixture();
  }

  public PathSegmentsFixture withIssue(SegmentDetail issue) {
    this.issue = issue;
    return this;
  }

  public PathSegmentsFixture withTarget(SegmentDetail target) {
    this.target = target;
    return this;
  }

  public PathSegmentsFixture withMacroSkill(SegmentDetail macroSkill) {
    this.macroSkill = macroSkill;
    return this;
  }

  public PathSegmentsFixture withSkill(SegmentDetail skill) {
    this.skill = skill;
    return this;
  }

  public PathSegments toModel() {
    return PathSegments.create(issue, target, macroSkill, skill);
  }
}
