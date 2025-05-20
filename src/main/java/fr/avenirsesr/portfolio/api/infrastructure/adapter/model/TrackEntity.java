package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "track")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class TrackEntity {
  @Id private UUID id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @ManyToMany private List<SkillLevelEntity> skillLevels;

  @ManyToMany private List<AMSEntity> amses;

  public static TrackEntity fromDomain(Track track) {
    return new TrackEntity(
        track.getId(),
        UserEntity.fromDomain(track.getUser()),
        track.getSkillLevels().stream().map(SkillLevelEntity::fromDomain).toList(),
        track.getAmses().stream().map(AMSEntity::fromDomain).toList());
  }
}
