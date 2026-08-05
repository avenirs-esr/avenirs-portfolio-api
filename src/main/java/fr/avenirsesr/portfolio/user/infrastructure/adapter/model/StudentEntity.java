package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.BIO_LENGTH;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

  @Column(name = "institution_id")
  private UUID institutionId;

  @Column(name = "group_id")
  private UUID groupId;

  @Column(length = BIO_LENGTH)
  private String bio;

  @Column(name = "has_unseen_notification", nullable = false)
  private boolean hasUnseenNotification;

  @Convert(converter = SelfKnowledgeCategoryListJsonConverter.class)
  @Column(name = "self_knowledge_categories", columnDefinition = "TEXT")
  private List<ESelfKnowledgeCategory> selfKnowledgeCategories = new ArrayList<>();

  @OneToOne
  @JoinColumn(name = "cover_picture_id")
  private FileEntity coverPicture;

  @OneToOne
  @JoinColumn(name = "profile_picture_id")
  private FileEntity profilePicture;

  private StudentEntity(
      UUID id,
      UserEntity user,
      String institutionEmail,
      UUID institutionId,
      UUID groupId,
      String bio,
      boolean hasUnseenNotification,
      List<ESelfKnowledgeCategory> selfKnowledgeCategories,
      FileEntity coverPicture,
      FileEntity profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    this.user = user;
    this.bio = bio;
    this.institutionEmail = institutionEmail;
    this.institutionId = institutionId;
    this.groupId = groupId;
    this.hasUnseenNotification = hasUnseenNotification;
    this.selfKnowledgeCategories = selfKnowledgeCategories;
    this.coverPicture = coverPicture;
    this.profilePicture = profilePicture;
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
  }

  public static StudentEntity of(
      UserEntity user,
      String institutionEmail,
      UUID institutionId,
      UUID groupId,
      String bio,
      boolean hasUnseenNotification,
      List<ESelfKnowledgeCategory> selfKnowledgeCategories,
      FileEntity coverPicture,
      FileEntity profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    return new StudentEntity(
        user.getId(),
        user,
        institutionEmail,
        institutionId,
        groupId,
        bio,
        hasUnseenNotification,
        selfKnowledgeCategories,
        coverPicture,
        profilePicture,
        createdAt,
        updatedAt);
  }
}
