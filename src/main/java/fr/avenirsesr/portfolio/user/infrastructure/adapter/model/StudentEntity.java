package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.BIO_LENGTH;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student")
@NoArgsConstructor
@Getter
@Setter
public class StudentEntity extends AvenirsBaseEntity {
  @OneToOne private UserEntity user;

  @Email
  @Column(nullable = false, name = "institution_email")
  private String institutionEmail;

  @Column(length = BIO_LENGTH)
  private String bio;

  @ManyToMany
  @JoinTable(
      name = "student_self_knowledge_category",
      joinColumns = @JoinColumn(name = "student_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<SelfKnowledgeCategoryEntity> selfKnowledgeCategories = new HashSet<>();

  private StudentEntity(UUID id, UserEntity user, String institutionEmail, String bio) {
    setId(id);
    this.user = user;
    this.bio = bio;
    this.institutionEmail = institutionEmail;
  }

  public static StudentEntity of(UserEntity user, String institutionEmail, String bio) {
    return new StudentEntity(user.getId(), user, institutionEmail, bio);
  }
}
