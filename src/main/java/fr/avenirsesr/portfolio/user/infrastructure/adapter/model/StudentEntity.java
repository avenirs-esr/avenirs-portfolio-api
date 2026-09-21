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

  @ElementCollection
  @CollectionTable(name = "student_institutions", joinColumns = @JoinColumn(name = "student_id"))
  @Column(name = "institution_id")
  private List<UUID> institutionIds = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "student_groups", joinColumns = @JoinColumn(name = "student_id"))
  @Column(name = "group_id")
  private List<UUID> groupIds = new ArrayList<>();

  @Column(length = BIO_LENGTH)
  private String bio;

  @Column(name = "has_unseen_notification", nullable = false)
  private boolean hasUnseenNotification;

  // The initializer is what the JPA/Lombok no-arg constructor relies on; PMD only sees the
  // all-args constructor overwriting it.
  @SuppressWarnings("PMD.UnusedAssignment")
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
      List<UUID> institutionIds,
      List<UUID> groupIds,
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
    this.institutionIds = institutionIds;
    this.groupIds = groupIds;
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
      List<UUID> institutionIds,
      List<UUID> groupIds,
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
        institutionIds,
        groupIds,
        bio,
        hasUnseenNotification,
        selfKnowledgeCategories,
        coverPicture,
        profilePicture,
        createdAt,
        updatedAt);
  }
}
