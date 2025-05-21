package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
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
@Table(name = "ams")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class AMSEntity {
  @Id private UUID id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @ManyToMany private List<SkillLevelEntity> skillLevels;

  public static AMSEntity fromDomain(AMS ams) {
    return new AMSEntity(
        ams.getId(),
        UserEntity.fromDomain(ams.getUser()),
        ams.getSkillLevels().stream().map(SkillLevelEntity::fromDomain).toList());
  }

  public static AMS toDomain(AMSEntity entity) {
    return AMS.toDomain(
            entity.getId(),
            UserEntity.toDomain(entity.getUser()),
            entity.getSkillLevels().stream().map(SkillLevelEntity::toDomain).toList()
    );
  }
}
