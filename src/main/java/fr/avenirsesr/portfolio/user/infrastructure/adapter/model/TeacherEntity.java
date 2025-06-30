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

  @Column private String profilePicture;

  @Column private String coverPicture;

  private TeacherEntity(String bio, boolean isActive, String profilePicture, String coverPicture) {
    this.bio = bio;
    this.isActive = isActive;
    this.profilePicture = profilePicture;
    this.coverPicture = coverPicture;
  }

  public static TeacherEntity of(
      String bio, boolean isActive, String profilePicture, String coverPicture) {
    return new TeacherEntity(bio, isActive, profilePicture, coverPicture);
  }
}
