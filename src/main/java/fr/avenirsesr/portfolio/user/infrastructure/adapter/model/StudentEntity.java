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
public class StudentEntity {

  @Column private String bio;

  @Column private boolean isActive;

  @Column private String profilePicture;

  @Column private String coverPicture;

  private StudentEntity(String bio, boolean isActive, String profilePicture, String coverPicture) {
    this.bio = bio;
    this.isActive = isActive;
    this.profilePicture = profilePicture;
    this.coverPicture = coverPicture;
  }

  public static StudentEntity of(
      String bio, boolean isActive, String profilePicture, String coverPicture) {
    return new StudentEntity(bio, isActive, profilePicture, coverPicture);
  }
}
