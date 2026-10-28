package fr.avenirsesr.portfolio.student.selfknowledge.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "self_knowledge_element",
    indexes = {
      @Index(name = "idx_ske_student_category", columnList = "student_id, self_knowledge_category")
    })
@NoArgsConstructor
@Getter
@Setter
public class SelfKnowledgeElementEntity extends AvenirsBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private StudentEntity student;

  @Column(nullable = false, length = TITLE_LENGTH)
  private String title;

  @Column(nullable = false, length = DESCRIPTION_LENGTH)
  private String description;

  @Column(nullable = true)
  @Min(1)
  @Max(5)
  private Integer rating;

  @Enumerated(EnumType.STRING)
  @Column(name = "self_knowledge_category", nullable = false)
  private ESelfKnowledgeCategory selfKnowledgeCategory;

  @Column(nullable = false)
  private boolean valorized;

  @ManyToMany
  @JoinTable(
      name = "self_knowledge_element_files",
      joinColumns = @JoinColumn(name = "self_knowledge_element_id"),
      inverseJoinColumns = @JoinColumn(name = "file_id"))
  @OrderBy("createdAt")
  private List<FileEntity> files = new ArrayList<>();

  @ElementCollection
  @CollectionTable(
      name = "self_knowledge_element_links",
      joinColumns = @JoinColumn(name = "self_knowledge_element_id"))
  @Column(name = "link", nullable = false)
  private List<@Size(max = LINK_LENGTH, message = "link can not exceed {max} characters") String>
      links = new ArrayList<>();

  private SelfKnowledgeElementEntity(
      UUID id,
      StudentEntity student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt,
      List<FileEntity> files,
      List<String> links) {
    this.setId(id);
    this.student = student;
    this.title = title;
    this.description = description;
    this.rating = rating;
    this.selfKnowledgeCategory = selfKnowledgeCategory;
    this.valorized = valorized;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.files = files;
    this.links = links;
  }

  public static SelfKnowledgeElementEntity of(
      UUID id,
      StudentEntity student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt,
      List<FileEntity> files,
      List<String> links) {
    return new SelfKnowledgeElementEntity(
        id,
        student,
        title,
        description,
        rating,
        selfKnowledgeCategory,
        valorized,
        createdAt,
        updatedAt,
        files,
        links);
  }
}
