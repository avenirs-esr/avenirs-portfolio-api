package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ProgramProgressServiceImpl implements ProgramProgressService {
  private static final int MAX_SKILLS = 6;
  private final ProgramProgressRepository programProgressRepository;

  private static SkillLevel findCurrentOrNextSkillLevel(Set<SkillLevel> skillLevels) {
    if (skillLevels == null || skillLevels.isEmpty()) {
      return null;
    }
    return skillLevels.stream()
        .filter(
            sl ->
                (sl.getStatus() == ESkillLevelStatus.UNDER_REVIEW
                        || sl.getStatus() == ESkillLevelStatus.UNDER_ACQUISITION)
                    && (sl.getEndDate() == null || sl.getEndDate().isAfter(LocalDate.now())))
        .findFirst()
        .orElseGet(
            () ->
                skillLevels.stream()
                    .filter(
                        sl ->
                            sl.getStatus() == ESkillLevelStatus.TO_BE_EVALUATED
                                || sl.getStatus() == ESkillLevelStatus.NOT_STARTED)
                    .findFirst()
                    .orElse(null));
  }

  private static Map<ProgramProgress, Set<Skill>> cleanProgramProgressList(
      List<ProgramProgress> programProgressList, boolean needToLimitSkills) {
    final int skillLimit =
        !programProgressList.isEmpty() ? MAX_SKILLS / programProgressList.size() : 0;

    return programProgressList.stream()
        .sorted(Comparator.comparing(p -> p.getProgram().getName()))
        .collect(
            Collectors.toMap(
                programProgress -> programProgress,
                programProgress ->
                    programProgress.getSkills().stream()
                        .sorted(Comparator.comparing(Skill::getName))
                        .limit(needToLimitSkills ? skillLimit : programProgress.getSkills().size())
                        .map(
                            skill -> {
                              SkillLevel selectedSkillLevel =
                                  findCurrentOrNextSkillLevel(skill.getSkillLevels());
                              Set<SkillLevel> selectedSkillLevelSet =
                                  selectedSkillLevel != null
                                      ? Set.of(selectedSkillLevel)
                                      : Set.of();
                              if (selectedSkillLevelSet.isEmpty()) {
                                return null;
                              }
                              return Skill.toDomain(
                                  skill.getId(),
                                  skill.getName(),
                                  selectedSkillLevelSet,
                                  skill.getLanguage());
                            })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                (existing, replacement) -> existing,
                LinkedHashMap::new));
  }

  @Override
  public Map<ProgramProgress, Set<Skill>> getSkillsOverview(Student student, ELanguage language) {
    return cleanProgramProgressList(
        programProgressRepository.findAllByStudent(student, language), true);
  }

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var programProgress = programProgressRepository.findAllAPCByStudent(student);
    return !programProgress.isEmpty();
  }

  @Override
  public Map<ProgramProgress, Set<Skill>> getSkillsView(Student student, ELanguage language) {
    return cleanProgramProgressList(
        programProgressRepository.findAllByStudent(student, language), false);
  }
}
