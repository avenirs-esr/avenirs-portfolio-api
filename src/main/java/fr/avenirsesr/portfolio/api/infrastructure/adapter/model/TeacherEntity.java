package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TeacherEntity {
  @Column private String bio;
  @Column private boolean isActive;
  @Column @Lob private byte[] profilePicture;
  @Column @Lob private byte[] coverPicture;
}
