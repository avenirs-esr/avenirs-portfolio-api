package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.model.DeletableAvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trace extends DeletableAvenirsBaseModel {
  private final User user;
  private String title;
  private List<SkillLevelProgress> skillLevels;
  private List<DeclaredSkillProgress> declaredSkillProgresses;
  private List<AMS> amses;
  private boolean isGroup;
  private ELanguage language;

  @Getter(AccessLevel.NONE)
  private String aiUseJustification;

  @Getter(AccessLevel.NONE)
  private String personalNote;

  private Trace(
      UUID id,
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String aiUseJustification,
      String personalNote,
      List<SkillLevelProgress> skillLevels,
      List<DeclaredSkillProgress> declaredSkillProgresses,
      List<AMS> amses,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    super(id, deletedAt, createdAt, updatedAt);
    this.user = user;
    this.title = title;
    this.language = language;
    this.skillLevels = skillLevels;
    this.declaredSkillProgresses = declaredSkillProgresses;
    this.amses = amses;
    this.isGroup = isGroup;
    this.aiUseJustification = aiUseJustification;
    this.personalNote = personalNote;
  }

  public static Trace create(
      UUID id,
      User user,
      String title,
      ELanguage language,
      boolean isGroup,
      String aiUseJustification,
      String personalNote) {

    return new Trace(
        id,
        user,
        title,
        language,
        isGroup,
        aiUseJustification,
        personalNote,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        Instant.now(),
        Instant.now(),
        null);
  }

  public static Trace toDomain(
      UUID id,
      User user,
      String title,
      List<SkillLevelProgress> skillLevels,
      List<DeclaredSkillProgress> declaredSkillProgresses,
      List<AMS> amses,
      boolean group,
      String aiUseJustification,
      String personalNote,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt,
      ELanguage language) {
    return new Trace(
        id,
        user,
        title,
        language,
        group,
        aiUseJustification,
        personalNote,
        skillLevels,
        declaredSkillProgresses,
        amses,
        createdAt,
        updatedAt,
        deletedAt);
  }

  public Optional<String> getAiUseJustification() {
    return Optional.ofNullable(aiUseJustification);
  }

  public Optional<String> getPersonalNote() {
    return Optional.ofNullable(personalNote);
  }

  public boolean isUnassociated() {
    return amses.isEmpty() && skillLevels.isEmpty() && declaredSkillProgresses.isEmpty();
  }

  public void add(AMS ams) {
    setAmses(Stream.concat(amses.stream(), Stream.of(ams)).toList());
  }

  public void add(SkillLevelProgress skillLevelProgress) {
    setSkillLevels(Stream.concat(skillLevels.stream(), Stream.of(skillLevelProgress)).toList());
  }

  public void add(DeclaredSkillProgress declaredSkillProgress) {
    setDeclaredSkillProgresses(
        Stream.concat(declaredSkillProgresses.stream(), Stream.of(declaredSkillProgress)).toList());
  }

  public void remove(DeclaredSkillProgress declaredSkillProgress) {
    var newDeclaredSkillProgresses = new ArrayList<>(declaredSkillProgresses);
    newDeclaredSkillProgresses.remove(declaredSkillProgress);
    setDeclaredSkillProgresses(newDeclaredSkillProgresses);
  }

  public void remove(SkillLevelProgress skillLevelProgress) {
    var newSkillLevel = new ArrayList<>(skillLevels);
    newSkillLevel.remove(skillLevelProgress);
    setSkillLevels(newSkillLevel);
  }

  public void remove(AMS ams) {
    var newAmses = new ArrayList<>(amses);
    newAmses.remove(ams);
    setAmses(newAmses);
  }
}
