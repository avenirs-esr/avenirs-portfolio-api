package fr.avenirsesr.portfolio.trace.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "trace")
@NoArgsConstructor
@Getter
@Setter
public class TraceEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ELanguage language;

  @ManyToMany
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinTable(
      name = "trace_skill_level",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private List<SkillLevelEntity> skillLevels;

  @ManyToMany
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinTable(
      name = "trace_ams",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "ams_id"))
  private List<AMSEntity> amses;

  @Column(nullable = false, name = "is_group")
  private boolean isGroup;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Size(max = 200, message = "ai use justification can not exceed 200 characters")
  @Column(name = "ai_use_justification")
  private String aiUseJustification;

  private TraceEntity(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      List<SkillLevelEntity> skillLevels,
      List<AMSEntity> amses,
      boolean isGroup,
      String aiUseJustification,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    this.setId(id);
    this.user = user;
    this.title = title;
    this.language = language;
    this.skillLevels = skillLevels;
    this.amses = amses;
    this.isGroup = isGroup;
    this.aiUseJustification = aiUseJustification;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.setDeletedAt(deletedAt);
  }

  public static TraceEntity of(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      List<SkillLevelEntity> skillLevels,
      List<AMSEntity> amses,
      boolean isGroup,
      String aiUseJustification,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    return new TraceEntity(
        id,
        user,
        title,
        language,
        skillLevels,
        amses,
        isGroup,
        aiUseJustification,
        createdAt,
        updatedAt,
        deletedAt);
  }
}
