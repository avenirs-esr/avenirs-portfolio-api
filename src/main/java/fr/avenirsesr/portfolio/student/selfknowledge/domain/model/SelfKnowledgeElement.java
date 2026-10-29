package fr.avenirsesr.portfolio.student.selfknowledge.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfKnowledgeElement extends AvenirsBaseModel {
  private final Student student;
  private String title;
  private String description;
  private Integer rating;
  private final ESelfKnowledgeCategory selfKnowledgeCategory;
  private boolean valorized;
  private List<File> files;
  private List<String> links;

  private SelfKnowledgeElement(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt,
      List<File> files,
      List<String> links) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.title = title;
    this.description = description;
    this.rating = rating;
    this.selfKnowledgeCategory = selfKnowledgeCategory;
    this.valorized = valorized;
    this.files = new ArrayList<>(files == null ? List.of() : files);
    this.links = new ArrayList<>(links == null ? List.of() : links);
  }

  public static SelfKnowledgeElement create(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory,
      List<String> links) {
    return new SelfKnowledgeElement(
        id,
        student,
        title,
        description,
        rating,
        selfKnowledgeCategory,
        false,
        Instant.now(),
        Instant.now(),
        List.of(),
        links);
  }

  public static SelfKnowledgeElement toDomain(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt,
      List<File> files,
      List<String> links) {
    return new SelfKnowledgeElement(
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

  public void addFile(File file) {
    this.files.add(file);
  }

  public void removeFile(UUID fileId) {
    this.files.removeIf(file -> file.getId().equals(fileId));
  }

  public Optional<File> getFile(UUID fileId) {
    return this.files.stream().filter(file -> file.getId().equals(fileId)).findFirst();
  }
}
