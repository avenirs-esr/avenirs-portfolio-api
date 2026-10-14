package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "activity_authorization",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_activity_authorization_activity_staff",
          columnNames = {"activity_id", "staff_id"})
    },
    indexes = {
      @Index(name = "idx_activity_authorization_activity", columnList = "activity_id"),
      @Index(name = "idx_activity_authorization_staff", columnList = "staff_id")
    })
@NoArgsConstructor
@Getter
@Setter
public class ActivityAuthorizationEntity extends AvenirsBaseEntity {
  @Column(name = "activity_id", nullable = false)
  private UUID activityId;

  @ManyToOne(optional = false)
  @JoinColumn(name = "staff_id", nullable = false)
  private StaffEntity staff;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "activity_authorization_rights",
      joinColumns = @JoinColumn(name = "activity_authorization_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "activity_right", nullable = false)
  private Set<EActivityRight> rights = new HashSet<>();

  public static ActivityAuthorizationEntity of(
      UUID id,
      UUID activityId,
      StaffEntity staff,
      Set<EActivityRight> rights,
      Instant createdAt,
      Instant updatedAt) {
    var entity = new ActivityAuthorizationEntity();
    entity.setId(id);
    entity.activityId = activityId;
    entity.staff = staff;
    entity.rights = new HashSet<>(rights);
    entity.setCreatedAt(createdAt);
    entity.setUpdatedAt(updatedAt);
    return entity;
  }
}
