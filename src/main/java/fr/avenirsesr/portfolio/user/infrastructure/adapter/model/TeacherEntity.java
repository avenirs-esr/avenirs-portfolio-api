package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class TeacherEntity {
  @Column private String bio;
  @Column private boolean isActive;

  private TeacherEntity(String bio, boolean isActive) {
    this.bio = bio;
    this.isActive = isActive;
  }

  public static TeacherEntity of(String bio, boolean isActive) {
    return new TeacherEntity(bio, isActive);
  }
}
