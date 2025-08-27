package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class PathSegmentsEmbeddable {

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "code", column = @Column(name = "skill_code")),
    @AttributeOverride(name = "libelle", column = @Column(name = "skill_libelle"))
  })
  private SegmentDetailEmbeddable skill;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "code", column = @Column(name = "macro_skill_code")),
    @AttributeOverride(name = "libelle", column = @Column(name = "macro_skill_libelle"))
  })
  private SegmentDetailEmbeddable macroSkill;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "code", column = @Column(name = "target_code")),
    @AttributeOverride(name = "libelle", column = @Column(name = "target_libelle"))
  })
  private SegmentDetailEmbeddable target;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "code", column = @Column(name = "issue_code")),
    @AttributeOverride(name = "libelle", column = @Column(name = "issue_libelle"))
  })
  private SegmentDetailEmbeddable issue;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "code", column = @Column(name = "domain_code")),
    @AttributeOverride(name = "libelle", column = @Column(name = "domain_libelle"))
  })
  private SegmentDetailEmbeddable domain;

  private PathSegmentsEmbeddable(
      SegmentDetailEmbeddable skill,
      SegmentDetailEmbeddable macroSkill,
      SegmentDetailEmbeddable target,
      SegmentDetailEmbeddable issue,
      SegmentDetailEmbeddable domain) {
    this.skill = skill;
    this.macroSkill = macroSkill;
    this.target = target;
    this.issue = issue;
    this.domain = domain;
  }

  public static PathSegmentsEmbeddable of(
      SegmentDetailEmbeddable skill,
      SegmentDetailEmbeddable macroSkill,
      SegmentDetailEmbeddable target,
      SegmentDetailEmbeddable issue,
      SegmentDetailEmbeddable domain) {
    return new PathSegmentsEmbeddable(skill, macroSkill, target, issue, domain);
  }
}
