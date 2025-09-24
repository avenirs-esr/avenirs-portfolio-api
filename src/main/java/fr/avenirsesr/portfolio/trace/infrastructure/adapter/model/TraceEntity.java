package fr.avenirsesr.portfolio.trace.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.DeletableAvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trace")
@NoArgsConstructor
@Getter
@Setter
public class TraceEntity extends DeletableAvenirsBaseEntity {
  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ELanguage language;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "trace_skill_level_progress",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_progress_id"))
  private List<SkillLevelProgressEntity> skillLevels;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "trace_ams",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "ams_id"))
  private List<AMSEntity> amses;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "trace_additional_skill_progress",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "additional_skill_progress_id"))
  private List<AdditionalSkillProgressEntity> additionalSkillsProgresses;

  @Column(nullable = false, name = "is_group")
  private boolean isGroup;

  @Size(max = 200, message = "ai use justification can not exceed 200 characters")
  @Column(name = "ai_use_justification")
  private String aiUseJustification;

  @Size(max = 200, message = "personal note can not exceed 200 characters")
  @Column(name = "personal_note")
  private String personalNote;

  private TraceEntity(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      List<SkillLevelProgressEntity> skillLevels,
      List<AdditionalSkillProgressEntity> additionalSkillsProgresses,
      List<AMSEntity> amses,
      boolean isGroup,
      String aiUseJustification,
      String personalNote,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    this.setId(id);
    this.setDeletedAt(deletedAt);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.user = user;
    this.title = title;
    this.language = language;
    this.skillLevels = skillLevels;
    this.additionalSkillsProgresses = additionalSkillsProgresses;
    this.amses = amses;
    this.isGroup = isGroup;
    this.aiUseJustification = aiUseJustification;
    this.personalNote = personalNote;
  }

  public static TraceEntity of(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      List<SkillLevelProgressEntity> skillLevels,
      List<AdditionalSkillProgressEntity> additionalSkillsProgress,
      List<AMSEntity> amses,
      boolean isGroup,
      String aiUseJustification,
      String personalNote,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    return new TraceEntity(
        id,
        user,
        title,
        language,
        skillLevels,
        additionalSkillsProgress,
        amses,
        isGroup,
        aiUseJustification,
        personalNote,
        createdAt,
        updatedAt,
        deletedAt);
  }
}
